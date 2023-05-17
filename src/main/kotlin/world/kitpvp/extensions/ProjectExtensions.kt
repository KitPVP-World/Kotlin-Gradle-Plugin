package world.kitpvp.extensions

import org.gradle.api.Project
import world.kitpvp.libraries.PluginLibraryHandlerScope
import world.kitpvp.libraries.pluginLibraryHandlerScopes

val Project.pluginLibraryHandlerScope: PluginLibraryHandlerScope
    get() = pluginLibraryHandlerScopes[this]!!

fun Project.pluginLibraries(configuration: PluginLibraryHandlerScope.() -> Unit) {
    configuration(pluginLibraryHandlerScope)
}