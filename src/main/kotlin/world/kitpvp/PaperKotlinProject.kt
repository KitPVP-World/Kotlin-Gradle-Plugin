package world.kitpvp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
class PaperKotlinProject: Plugin<Project> {
    override fun apply(target: Project) {
        /**
         * Globals
         */
        val extension = target.extensions
            .create("paperKotlin", PaperKotlinExtension::class.java)

        val pluginYml: Boolean by target.extra(true)
        val runTasks: Boolean by target.extra(true)
        val paperShelled: Boolean by target.extra(false)

        target.apply {
            /**
             * Apply plugins
             */
            plugin("org.jetbrains.kotlin:kotlin-jvm")
            plugin("org.gradle.java-library")
            plugin("io.papermc.paperweight.userdev")

            if(pluginYml) {
                plugin("net.minecrell.plugin-yml.bukkit") // Generates plugin.yml
            }
            if(runTasks) {
                plugin("dev.s7a.gradle.minecraft.server") // Generates run tasks
            }
            if(paperShelled) {
                plugin("cn.apisium.papershelled") // Mixin support for paper
            }
        }

        applyToProject(target, extension, pluginYml, runTasks, paperShelled)
    }
}