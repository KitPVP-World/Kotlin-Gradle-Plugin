package world.kitpvp.libraries

import org.gradle.api.Project
import world.kitpvp.KotlinGradleExtension

val pluginLibraryHandlerScopes = mutableMapOf<Project, PluginLibraryHandlerScope>()

/**
 * Scope for adding plugin libraries
 */
class PluginLibraryHandlerScope(
    val extension: KotlinGradleExtension
) {

    /**
     * All plugin libraries
     */
    val pluginLibraries = mutableListOf<String>()

    /**
     * All plugin implementations
     */
    val libraryImplementations = mutableListOf<String>()

    /**
     * All plugin compileOnlies
     */
    val libraryCompileOnlies = mutableListOf<String>()

    var usingSimpleCloud = false
    var usingASWM = false
    var usingPaperShelled = false
    var usingPaperServer = false
    var createPluginYml = false
    var generateRunTasks = false
    var usingPaper = false


    /**
     * Adds a dependency using implementation and adds it to the plugin's libraries
     * @param dependencyNotation the dependency notation e.g. world.kitpvp:core-api:1.0.0
     */
    @Suppress("unused")
    fun pluginLibraryImplementation(dependencyNotation: String) {
        pluginLibrary(dependencyNotation)
        libraryImplementation(dependencyNotation)
    }

    /**
     * Adds a dependency using compileOnly and adds it to the plugin's libraries
     * @param dependencyNotation the dependency notation e.g. world.kitpvp:core-api:1.0.0
     */
    @Suppress("unused")
    fun pluginLibraryCompileOnly(dependencyNotation: String) {
        pluginLibrary(dependencyNotation)
        libraryCompileOnly(dependencyNotation)
    }

    /**
     * Simple implementation method from gradle (Specifically used for in library stuff)
     */
    @Suppress("unused")
    private fun libraryImplementation(dependencyNotation: String) {
        libraryImplementations += dependencyNotation
    }

    /**
     * Simple compileOnly method from gradle (Specifically used for in library stuff)
     */
    @Suppress("unused")
    fun libraryCompileOnly(dependencyNotation: String) {
        libraryCompileOnlies += dependencyNotation
    }
    /**
     * Simply adds a library to the plugin's libraries
     * @param dependencyNotation the dependency notation e.g. world.kitpvp:core-api:1.0.0
     */
    @Suppress("unused")
    fun pluginLibrary(dependencyNotation: String) {
        pluginLibraries += dependencyNotation
    }

    /**
     * Adds a kotlin module as plugin library implementation to the project
     */
    @Suppress("unused")
    fun kotlinPluginLibrary(module: String) {
        pluginLibraryImplementation(kotlin(module))
    }

    /**
     * Adds jetbrains exposed framework as plugin library implementation to the project
     * @param modules What modules / artifacts of exposed should be used (Without core)
     * @param exposedVersion Latest version available at [Exposed#releases](https://github.com/JetBrains/Exposed/releases/latest)
     */
    @Suppress("unused")
    fun exposedPluginLibrary(exposedVersion: String, modules: List<String> = listOf("dao", "jdbc", "java-time")) {
        val exposedPrefix = "org.jetbrains.exposed:exposed"

        pluginLibraryImplementation("$exposedPrefix-core:$exposedVersion")
        for (module in modules) {
            pluginLibraryImplementation("$exposedPrefix-$module:$exposedVersion")
        }
    }

    /**
     * Adds paper to the project
     * @param paperServer If true it will apply the paperweight userdev plugin to the project and adds the paper dependency else it will apply the paper api for lightweight development
     * @param pluginYml Automatically generate plugin.yml
     * @param runTasks Generate run task: runServer
     */
    @Suppress("unused")
    fun paper(paperServer: Boolean = true, pluginYml: Boolean = true, runTasks: Boolean) {
        usingPaper = true

        if(paperServer) {
            usingPaperServer = true
        } else
            libraryCompileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

        if(pluginYml) createPluginYml = true
        if(runTasks) generateRunTasks = true

    }

    /**
     * Adds kspigot as plugin library implementation to the project
     * @param kspigotVersion Latest version available at [KSpigot#releases](https://github.com/jakobkmar/KSpigot/releases/latest)
     */
    @Suppress("unused")
    fun kspigotPluginLibrary(kspigotVersion: String) {
        pluginLibraryImplementation("net.axay:kspigot:$kspigotVersion")
    }

    /**
     * Adds simple cloud as implementation to the project
     * @param modules What modules / artifacts of simple cloud should be used (Without api & plugin)
     */
    @Suppress("unused")
    fun simpleCloudPluginLibrary(simpleCloudVersion: String, modules: List<String> = emptyList()) {
        usingSimpleCloud = true
        val simpleCloudPrefix = "eu.thesimplecloud.simplecloud:simplecloud"

        libraryImplementation("$simpleCloudPrefix-api:$simpleCloudVersion")
        libraryImplementation("$simpleCloudPrefix-plugin:$simpleCloudVersion")
        for (module in modules) {
            libraryImplementation("$simpleCloudPrefix-$module:$simpleCloudVersion")
        }
    }

    /**
     * Adds advanced slime world manager as implementation to the project
     * @param modules What modules / artifacts of aswm should be used (Without api)
     */
    @Suppress("unused")
    fun aswmPluginLibrary(modules: List<String> = emptyList()) {
        usingASWM = true

        val aswmGroup = "com.infernalsuite.aswm"
        val slimeVersion = extension.slimeVersion

        libraryImplementation("$aswmGroup:api:$slimeVersion")
        for (module in modules) {
            libraryImplementation("$aswmGroup:$module:$slimeVersion")
        }
    }

    /**
     * * Alias of #paperShelledPluginLibrary(version)
     * * Adds paper shelled / mixins as plugin to the project
     * @param paperShelledVersion Latest version available at [PaperShelled#releases](https://github.com/Apisium/PaperShelled/releases/latest)
     */
    @Suppress
    fun mixinsPluginLibrary(paperShelledVersion: String = "1.0.0") = paperShelledPluginLibrary(paperShelledVersion)

    /**
     * Adds paper shelled / mixins as plugin to the project
     * @param paperShelledVersion Latest version available at [PaperShelled#releases](https://github.com/Apisium/PaperShelled/releases/latest)
     */
    @Suppress
    fun paperShelledPluginLibrary(paperShelledVersion: String = "1.0.0") {
        usingPaperShelled = true
    }



    /**
     * Convert a kotlin module name to a dependency notation
     * @param module A kotlin module like reflect
     * @return Dependency notation of specified module (org.jetbrains.kotlin:kotlin-$module)
     */
    fun kotlin(module: String): String {
        return "org.jetbrains.kotlin:kotlin-$module"
    }
}