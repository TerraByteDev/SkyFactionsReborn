package net.skullian.skyfactions.common.libraries.flavor.reflections

import net.skullian.skyfactions.common.libraries.flavor.FlavorOptions
import org.reflections.Reflections
import org.reflections.Store
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.Scanners
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.QueryFunction
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
class PackageIndexer(
    private val clazz: KClass<*>,
    options: FlavorOptions
)
{
    val reflections =
        Reflections(
            ConfigurationBuilder()
                .forPackage(
                    options.`package` ?: this.clazz.java.`package`.name,
                    this.clazz.java.classLoader
                )
                .addScanners(
                    MethodAnnotationsScanner(),
                    TypeAnnotationsScanner(),
                    SubTypesScanner(false)
                )
        )

    init {
        fun getAllClassesInPackage(packageName: String): Set<Class<*>> {
            val reflections = Reflections(
                ConfigurationBuilder()
                    .forPackage(packageName)
                    .addScanners(SubTypesScanner(false))
            )

            return reflections.getSubTypesOf(Any::class.java)
        }

        val classes = getAllClassesInPackage("net.skullian.skyfactions.api.service")
        classes.forEach { println(it.name) }
    }

    /**
     * Returns a list of subtypes of the specified type.
     *
     * @param T the type whose subtypes are to be retrieved
     * @return a list of subtypes of the specified type
     */
    inline fun <reified T> getSubTypes(): List<Class<*>>
    {
        return reflections
            .get(subTypes<T>())
            .toList()
    }

    /**
     * Returns a list of methods annotated with the specified annotation.
     *
     * @param T the annotation type
     * @return a list of methods annotated with the specified annotation
     */
    inline fun <reified T : Annotation> getMethodsAnnotatedWith(): List<Method>
    {
        return reflections
            .get(annotated<T>())
            .toList()
    }

    /**
     * Returns a list of types annotated with the specified annotation.
     *
     * @param T the annotation type
     * @return a list of types annotated with the specified annotation
     */
    inline fun <reified T : Annotation> getTypesAnnotatedWith(): List<Class<*>>
    {
        return reflections
            .getTypesAnnotatedWith(T::class.java)
            .toList()
    }

    /**
     * Returns a query function for methods annotated with the specified annotation.
     *
     * @param T the annotation type
     * @return a query function for methods annotated with the specified annotation
     */
    inline fun <reified T> annotated(): QueryFunction<Store, Method>
    {
        return Scanners.MethodsAnnotated
            .with(T::class.java)
            .`as`(Method::class.java)
    }

    /**
     * Returns a query function for subtypes of the specified type.
     *
     * @param T the type whose subtypes are to be retrieved
     * @return a query function for subtypes of the specified type
     */
    inline fun <reified T> subTypes(): QueryFunction<Store, Class<*>>
    {
        return Scanners.SubTypes
            .with(T::class.java)
            .`as`(Class::class.java)
    }
}