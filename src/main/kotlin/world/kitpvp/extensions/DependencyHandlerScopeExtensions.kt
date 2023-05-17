package world.kitpvp.extensions

import gradle.kotlin.dsl.accessors._46d043dc7409397208137acfa1647ba6.implementation
import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.kotlin
import world.kitpvp.PaperKotlinExtension

/**
 * All plugin libraries
 */
val pluginLibraries = mutableListOf<String>()

var usingSimpleCloud = false

/**
 * Adds a dependency using implementation and adds it to the plugin's libraries
 * @param dependencyNotation the dependency notation e.g. world.kitpvp:core-api:1.0.0
 */
fun DependencyHandlerScope.pluginLibraryImplementation(dependencyNotation: String): Dependency? {
    pluginLibraries += dependencyNotation
    return implementation(dependencyNotation)
}

@Suppress("unused")
/**
 * Adds kotlin reflect as plugin library implementation to the project
 */
fun DependencyHandlerScope.kotlinReflectPluginLib() {
    pluginLibraryImplementation(kotlin("reflect").toString())
}

@Suppress("unused")
/**
 * Adds jetbrains exposed framework as plugin library implemetation to the project
 * @param modules What modules / artifacts of exposed should be used (Without core)
 * @param exposedVersion the version of exposed
 */
fun DependencyHandlerScope.exposedPluginLib(exposedVersion: String, modules: List<String> = listOf("dao", "jdbc", "java-time")) {
    val exposedPrefix = "org.jetbrains.exposed:exposed"

    pluginLibraryImplementation("$exposedPrefix-core:$exposedVersion")
    for (module in modules) {
        pluginLibraryImplementation("$exposedPrefix-$module:$exposedVersion")
    }
}

@Suppress("unused")
/**
 * Adds simple cloud as implemetation to the project
 * @param modules What modules / artifacts of simplecloud should be used (Without api & plugin)
 */
fun DependencyHandlerScope.simpleCloudPluginLib(simpleCloudVersion: String, modules: List<String> = emptyList()) {
    usingSimpleCloud = true
    val simpleCloudPrefix = "eu.thesimplecloud.simplecloud:simplecloud"

    implementation("$simpleCloudPrefix-api:$simpleCloudVersion")
    implementation("$simpleCloudPrefix-plugin:$simpleCloudVersion")
    for (module in modules) {
        implementation("$simpleCloudPrefix-$module:$simpleCloudVersion")
    }
}

@Suppress("unused")
/**
 * Adds advanced slime world manager as implementation to the project
 * @param modules What modules / artifacts of aswm should be used (Without api)
 */
fun DependencyHandlerScope.aswmPluginLib(modules: List<String> = emptyList()) {
    val aswmGroup = "com.infernalsuite.aswm"
    val slimeVersion = extensions.getByType(PaperKotlinExtension::class.java).slimeVersion

    implementation("$aswmGroup:api:$slimeVersion")
    for (module in modules) {
        implementation("$aswmGroup:$module:$slimeVersion")
    }
}