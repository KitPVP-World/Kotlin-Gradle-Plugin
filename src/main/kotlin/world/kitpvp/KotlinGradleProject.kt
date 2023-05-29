package world.kitpvp

import org.gradle.api.Plugin
import org.gradle.api.Project
import world.kitpvp.libraries.PluginLibraryHandlerScope
import world.kitpvp.libraries.pluginLibraryHandlerScopes

@Suppress("unused")
class KotlinGradleProject: Plugin<Project> {

    override fun apply(target: Project) {
        /**
         * Globals
         */

        val extension = target.extensions
            .create("paperKotlin", KotlinGradleExtension::class.java)

        val pluginLibraryHandlerScope = PluginLibraryHandlerScope(extension)
        pluginLibraryHandlerScopes[target] = pluginLibraryHandlerScope

        /**
         * Apply plugins
         */
        // TODO: Do I even need to do this?
        target.apply {
            plugin("org.jetbrains.kotlin.jvm")
            plugin("org.gradle.java-library")
        }

        target.applyToProject(extension)
    }



}