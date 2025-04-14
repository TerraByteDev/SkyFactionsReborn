package net.skullian.skyfactions.api.hook

import net.skullian.skyfactions.api.exception.HookException
import net.skullian.skyfactions.api.library.flavor.service.Service
import net.skullian.skyfactions.api.hook.annotation.HookData
import net.skullian.skyfactions.api.library.flavor.service.Close
import net.skullian.skyfactions.api.library.flavor.service.Configure
import java.io.IOException
import java.net.URL
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * An interface for the hook manager.
 * This is used to load and manage hooks.
 */
@Service(
    name = "Hook Manager"
)
interface HookManager {

    val hooks: MutableMap<HookLoadOrder, MutableList<SkyHook>>
        get() = mutableMapOf()
    val loadedHooks: MutableList<Class<*>>
        get() = mutableListOf()

    /**
     * This will be implemented by the platform to return the path to the hooks.
     * This will differ for all platforms as they have different ways of loading plugins, and hooks won't be
     * platform independent.
     */
    fun getHookLocation(): String

    /**
     * This will be called by the platform to load the hooks.
     * This involves scanning the [getHookLocation] package for [SkyHook] instances.
     */
    fun load() {
        val loader = javaClass.classLoader

        try {

            val path = getHookLocation()
            val resources = loader.getResources(path)

            while (resources.hasMoreElements()) {
                val resource: URL = resources.nextElement()

                if (resource.protocol.equals("jar")) {
                    val resourcePath = resource.path
                        .substring(5, resource.path.indexOf("!"))
                        .replace("%20", "")
                    val file = JarFile(resourcePath)

                    val entries = file.entries()
                    handleJarEntries(resourcePath, entries, loader)

                    file.close()
                }
            }

        } catch (ignored: ClassNotFoundException) {
        } catch (exception: IOException) {
            throw HookException(exception)
        }
    }

    @Suppress("NestedBlockDepth")
    private fun handleJarEntries(resourcePath: String, entries: Enumeration<JarEntry>, loader: ClassLoader) {
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name.startsWith(resourcePath) && entry.name.endsWith(".class")) {
                val className = entry.name.substring(0, entry.name.length - 6).replace('/', '.')
                try {
                    val clazz = loader.loadClass(className)

                    if (SkyHook::class.java.isAssignableFrom(clazz)) {
                        val hook = clazz.getDeclaredConstructor().newInstance() as? SkyHook
                        if (hook != null) {
                            val loadOrder = getHookLoadOrder(hook)
                            hooks.getOrPut(loadOrder) { mutableListOf() }.add(hook)
                        }
                    }
                } catch (ignored: Exception) {}
            }
        }
    }

    /**
     * This will be called by the platform to initialize the hooks.
     * @param order The current order of hooks to be loaded. This method is called multiple times for each [HookLoadOrder] stage.
     */
    @Configure
    fun initialize(order: HookLoadOrder)

    /**
     * Called when the platform disables (typically server shutdown).
     */
    @Close
    fun onDisable()

    /**
     * Fetch a loaded [SkyHook] by its class.
     *
     * @return The loaded hook class, if present.
     */
    fun getLoadedHook(hook: Class<*>): SkyHook? {
        return loadedHooks.stream()
            .filter { loadedHook -> loadedHook == hook }
            .findFirst()
            .orElse(null) as? SkyHook
    }

    private fun getHookLoadOrder(hook: SkyHook): HookLoadOrder {
        require(hook.javaClass.isAnnotationPresent(HookData::class.java)) { "Hook ${hook.javaClass.name} does not have HookData annotation" }

        val annotation = hook.javaClass.getAnnotation(HookData::class.java)
        return annotation.loadOrder
    }

    /**
     * Get the plugin name that the specific [SkyHook] depends on.
     */
    fun getHookPluginName(hook: SkyHook): String {
        // no need to check, as it will be checked in the getHookLoadOrder method
        val annotation = hook.javaClass.getAnnotation(HookData::class.java)
        return annotation.pluginName
    }

}