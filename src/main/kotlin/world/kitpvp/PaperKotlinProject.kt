package world.kitpvp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import world.kitpvp.libraries.PluginLibraryHandlerScope
import world.kitpvp.libraries.pluginLibraryHandlerScopes

@Suppress("unused")
class PaperKotlinProject: Plugin<Project> {

    override fun apply(target: Project) {
        /**
         * Globals
         */

        val extension = target.extensions
            .create("paperKotlin", PaperKotlinExtension::class.java)

        val pluginLibraryHandlerScope = PluginLibraryHandlerScope(extension)
        pluginLibraryHandlerScopes[target] = pluginLibraryHandlerScope

        val pluginYml: Boolean by target.extra(true)
        val runTasks: Boolean by target.extra(true)
        val paper: Boolean by target.extra(true)
        val mixins: Boolean by target.extra(false)

        /**
         * Apply plugins
         */
        target.apply {
            plugin("org.jetbrains.kotlin.jvm")
            plugin("org.gradle.java-library")
            plugin("io.papermc.paperweight.userdev")

            if(pluginYml) {
                plugin("net.minecrell.plugin-yml.bukkit") // Generates plugin.yml
            }
            if(runTasks) {
                plugin("dev.s7a.gradle.minecraft.server") // Generates run tasks
            }
            if(mixins) {
                plugin("cn.apisium.papershelled") // Mixin support for paper
            }
        }

        target.applyToProject(extension, paper, pluginYml, runTasks)
    }



}