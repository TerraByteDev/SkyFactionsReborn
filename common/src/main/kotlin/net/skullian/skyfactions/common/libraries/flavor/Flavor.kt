package net.skullian.skyfactions.common.libraries.flavor

import net.skullian.skyfactions.api.library.flavor.service.Close
import net.skullian.skyfactions.api.library.flavor.service.Configure
import net.skullian.skyfactions.common.libraries.flavor.binder.FlavorBinderContainer
import net.skullian.skyfactions.common.libraries.flavor.reflections.PackageIndexer
import net.skullian.skyfactions.api.library.flavor.service.Service
import net.skullian.skyfactions.api.library.flavor.service.ignore.IgnoreAutoScan
import net.skullian.skyfactions.common.libraries.flavor.inject.Inject
import net.skullian.skyfactions.common.util.FlavorException
import net.skullian.skyfactions.common.util.SLogger
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 1/2/2022
 */
class Flavor(
    val initializer: KClass<*>,
    val options: FlavorOptions
)
{
    companion object
    {
        /**
         * Creates a new [Flavor] instance using [T]'s [KClass],
         * and the [options], if any are given.
         *
         * @param T the type of the flavor
         * @param options the flavor options
         * @return a new [Flavor] instance
         */
        @JvmStatic
        inline fun <reified T> create(
            options: FlavorOptions = FlavorOptions()
        ): Flavor
        {
            return Flavor(T::class, options)
        }

        /**
         * Creates a new [Flavor] instance using the specified [initializer] and [options].
         *
         * @param initializer the class used to initialize the flavor
         * @param options the flavor options
         * @return a new [Flavor] instance
         */
        @JvmStatic
        fun create(
            initializer: KClass<*>,
            options: FlavorOptions = FlavorOptions()
        ): Flavor {
            return Flavor(initializer, options)
        }
    }

    val reflections = PackageIndexer(initializer, options)

    val binders = mutableListOf<FlavorBinder<*>>()
    val services = mutableMapOf<KClass<*>, Any>()

    val scanners =
        mutableMapOf<KClass<out Annotation>, (Method, Any) -> Unit>()

    /**
     * Registers a listener for the specified annotation type.
     *
     * @param T the annotation type
     * @param lambda the function to be invoked when the annotation is found
     */
    inline fun <reified T : Annotation> listen(
        noinline lambda: (Method, Any) -> Unit
    )
    {
        scanners[T::class] = lambda
    }

    /**
     * Inherits an arbitrary [FlavorBinderContainer] and populates our binders with its ones.
     *
     * @param container the flavor binder container to inherit
     * @return the current [Flavor] instance
     */
    fun inherit(container: FlavorBinderContainer): Flavor
    {
        container.populate()
        binders += container.binders
        return this
    }

    /**
     * Searches for and returns a service matching type [T].
     *
     * @param T the service type
     * @return the service instance
     * @throws RuntimeException if there is no service matching type [T]
     */
    inline fun <reified T> service(): T
    {
        val service = services[T::class]
            ?: throw FlavorException(Throwable("A non-service class was provided."))

        return service as T
    }

    /**
     * Creates a new [FlavorBinder] for type [T].
     *
     * @param T the type to bind
     * @return a new [FlavorBinder] instance
     */
    inline fun <reified T : Any> bind(): FlavorBinder<T>
    {
        val binder = FlavorBinder(T::class)
        binders.add(binder)

        return binder
    }

    /**
     * Finds and returns a list of singletons annotated with the specified annotation.
     *
     * @param A the annotation type
     * @return a list of singletons annotated with the specified annotation
     */
    inline fun <reified A : Annotation> findSingletons(): List<Any>
    {
        return reflections.getTypesAnnotatedWith<A>()
            .mapNotNull { it.objectInstance() }
            .filter {
                it.javaClass.isAnnotationPresent(A::class.java)
            }
    }

    /**
     * Creates and injects a new instance of [T].
     *
     * @param T the type to inject
     * @param params the parameters for the constructor
     * @return a new injected instance of [T]
     */
    inline fun <reified T : Any> injected(
        vararg params: Any
    ): T
    {
        val instance = T::class.java.let { clazz ->
            if (params.isEmpty())
            {
                clazz.newInstance()
            } else
            {
                clazz.getConstructor(
                    *params.map { it.javaClass }.toTypedArray()
                ).newInstance(
                    *params.toList().toTypedArray()
                )
            }
        }

        inject(instance)
        return instance
    }

    /**
     * Injects fields into a pre-existing class.
     *
     * @param any the object to inject fields into
     */
    fun inject(any: Any)
    {
        scanAndInject(any::class, any)
    }

    /**
     * Scans & injects any services and/or singletons (kt objects)
     * that contain fields annotated with [Inject].
     */
    fun startup()
    {
        val classes = reflections
            .getTypesAnnotatedWith<Service>()
            .sortedByDescending {
                it.getAnnotation(Service::class.java)
                    ?.priority ?: 1
            }

        for (clazz in classes)
        {
            val ignoreAutoScan = clazz
                .getAnnotation(
                    IgnoreAutoScan::class.java
                )

            if (ignoreAutoScan == null)
            {
                kotlin.runCatching {
                    scanAndInject(clazz.kotlin, clazz.objectInstance())
                }.onFailure {
                    SLogger.warn("An exception was thrown during injection, {}", it)
                }
            }
        }
    }

    /**
     * Invokes the `close` method in all registered services. If a
     * service does not have a close method, the service will be skipped.
     */
    fun close()
    {
        for (entry in services.entries)
        {
            val close = entry.key.java.declaredMethods
                .firstOrNull { it.isAnnotationPresent(Close::class.java) }

            val service = entry.key.java
                .getDeclaredAnnotation(Service::class.java)

            val milli = tracked {
                close?.invoke(entry.value)
            }

            if (milli != -1L)
            {
                SLogger.info("Shutdown service [${
                    service.name.ifEmpty { 
                        entry.key.java.simpleName
                    }
                }] in ${milli}ms")
            } else
            {
                SLogger.fatal("Failed to shutdown service [${
                    service.name.ifEmpty { 
                        entry.key.java.simpleName
                    }
                }]!")
            }
        }
    }

    /**
     * Invokes the provided [lambda] while keeping track of
     * the amount of time it took to run in milliseconds.
     *
     * Any exception thrown within the lambda will be printed,
     * and `-1` will be returned.
     */
    private fun tracked(lambda: () -> Unit): Long
    {
        val start = System.currentTimeMillis()

        try
        {
            lambda.invoke()
        } catch (exception: Exception)
        {
            SLogger.fatal("Failed to invoke lambda: {}", exception)
            return -1
        }

        return System.currentTimeMillis() - start
    }

    /**
     * Finds an instance for injection based on the specified type and annotations.
     *
     * @param type the class type
     * @param annotations the annotations
     * @return the instance for injection
     * @throws IllegalArgumentException if no binder is found for the specified type and annotations
     */
    fun findInstanceForInjection(type: Class<*>, annotations: Array<Annotation>): Any
    {
        // trying to find [FlavorBinder]s
        // of the field's type
        val bindersOfType = binders
            .filter { it.kClass.java == type }
            .toMutableList()

        for (flavorBinder in bindersOfType)
        {
            for (annotation in annotations)
            {
                // making sure if there are any annotation
                // checks, that the field passes the check
                flavorBinder.annotationChecks[annotation::class]
                    ?.let {
                        val passesCheck = it.invoke(annotation)

                        if (!passesCheck)
                        {
                            bindersOfType.remove(flavorBinder)
                        }
                    }
            }
        }

        // retrieving the first binder of the field's type
        return bindersOfType.firstOrNull()
            ?: throw IllegalArgumentException(
                "Did not find any binder for type ${type.simpleName} that satisfies $annotations"
            )
    }

    /**
     * Scans & injects a provided [KClass], along with its
     * singleton instance if there is one.
     */
    private fun scanAndInject(clazz: KClass<*>, instance: Any? = null)
    {
        // use the provided instance, or the singleton
        // we got through KClass#objectInstance.
        val singleton = instance
            ?: clazz.java.objectInstance()
            ?: return

        for (field in clazz.java.declaredFields)
        {
            // making sure this field is annotated with
            // Inject before modifying its value.
            if (field.isAnnotationPresent(Inject::class.java))
            {
                val injectionInstance = this
                    .findInstanceForInjection(
                        field.type, field.annotations
                    )

                val accessibility = field.isAccessible

                field.isAccessible = false
                field.set(singleton, injectionInstance)
                field.isAccessible = accessibility
            }
        }

        for (method in clazz.java.declaredMethods)
        {
            val annotations = method.annotations
                .filter { scanners[it::class] != null }

            for (annotation in annotations)
            {
                try
                {
                    scanners[annotation::class]
                        ?.invoke(method, singleton)
                } catch (exception: Exception)
                {
                    SLogger.fatal("An error occurred while invoking function - {}", exception)
                }
            }
        }

        // checking if this class is a service
        val isServiceClazz = clazz.java
            .isAnnotationPresent(Service::class.java)

        if (isServiceClazz)
        {
            val configure = clazz.java.declaredMethods
                .firstOrNull { it.isAnnotationPresent(Configure::class.java) }

            // singletons should always be non-null
            services[clazz] = singleton

            val service = clazz.java
                .getDeclaredAnnotation(Service::class.java)

            val milli = tracked {
                configure?.invoke(singleton)
            }

            // making sure an exception wasn't thrown
            // while trying to configure the service
            if (milli != -1L)
            {
                SLogger.info("Loaded service [${
                    service.name.ifEmpty { 
                        clazz.java.simpleName
                    }
                }] in ${milli}ms")
            } else
            {
                SLogger.fatal("Failed to load service [${
                    service.name.ifEmpty { 
                        clazz.java.simpleName
                    }
                }]!")
            }
        }
    }

    /**
     * Returns the singleton instance of the class, if any.
     *
     * @return the singleton instance, or null if there is none
     */
    fun Class<*>.objectInstance(): Any?
    {
        return kotlin
            .runCatching {
                getDeclaredField("INSTANCE").get(null)
            }
            .getOrNull()
    }
}