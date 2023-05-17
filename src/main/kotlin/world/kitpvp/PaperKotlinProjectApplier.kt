package world.kitpvp

import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import gradle.kotlin.dsl.accessors._1622db761ce1e4742dacab1462fe07fd.*
import gradle.kotlin.dsl.accessors._1622db761ce1e4742dacab1462fe07fd.paperweight
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import world.kitpvp.extensions.pluginLibraryHandlerScope
import world.kitpvp.utils.ASWM
import java.io.FileOutputStream

fun Project.applyToProject(extension: PaperKotlinExtension, paper: Boolean, pluginYml: Boolean, runTasks: Boolean) {
    val pluginLibraryHandlerScope = project.pluginLibraryHandlerScope

    /**
     * Configure Plugins
     */

    // Configure plugin.yml defaults
    if(pluginYml) {
        bukkit {
            apiVersion = extension.apiVersion
            libraries = pluginLibraryHandlerScope.pluginLibraries
            load = BukkitPluginDescription.PluginLoadOrder.STARTUP
            main = "$group.${project.name.lowercase()}.${project.name}"
            description = project.description
            version = project.version.toString()
        }

    }
    // Configure mixin support
    if(pluginLibraryHandlerScope.usingPaperShelled) {
        println("Using paper shelled")

        paperShelled {
            archiveClassifier.set("mixins")
            jarUrl.set("")
        }

        buildscript {
            repositories {
                maven("https://maven.fabricmc.net/")
            }
        }
    }

    /**
     * Dependencies
     */
    repositories {
        mavenCentral()

        if(paper)
            maven("https://repo.papermc.io/repository/maven-public/")

        if(pluginLibraryHandlerScope.usingSimpleCloud)
            maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local")

        if(pluginLibraryHandlerScope.usingASWM)
            maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
    }


    dependencies {
        if(paper) {
            val minecraft = extension.minecraftVersion

            paperweight.paperDevBundle("$minecraft-R0.1-SNAPSHOT")
        }
    }

    /**
     * Task Configurations
     */
    tasks {
        // Configure reobfJar to run when invoking the build task
        assemble {
            dependsOn(reobfJar)
        }

        if(runTasks) {
            // Minecraft Server
            task<LaunchMinecraftServerTask>("runServer") {
                dependsOn("reloadPlugin")

                val slimeVersion by lazy { extension.slimeVersion }
                val slimeBuild by lazy { extension.slimeBuild }
                var ready = true

                doFirst {
                    if(!ready) {
                        throw GradleException("No slime specified! Server won't start.")
                    }
                    if(pluginLibraryHandlerScope.usingSimpleCloud)
                        logger.warn("Simple Cloud implementation detected! This plugin may not work on a normal server")

                    val slimeJarFileOutputDir = buildDir.resolve("MinecraftServer/plugins/")
                    slimeJarFileOutputDir.mkdirs()
                    val slimeJarFileOutput = slimeJarFileOutputDir.resolve("AdvancedSlimeManager.jar")
                    if(!slimeJarFileOutput.exists()) {
                        slimeJarFileOutput.createNewFile()
                    }

                    uri(ASWM.plugin(slimeBuild, slimeVersion))
                        .toURL().openStream().use { it.copyTo(FileOutputStream(slimeJarFileOutput)) }
                }

                afterEvaluate {
                    if(!extension.isSlimeBuildInitialized()) {
                        ready = false
                        logger.warn("No slime build specified! Server won't start.")
                        return@afterEvaluate
                    }
                    jarUrl.set(ASWM.server(slimeBuild, slimeVersion))
                    agreeEula.set(true)
                }

            }
            create("reloadPlugin") {
                dependsOn("build")
                doLast {
                    copy {
                        from(buildDir.resolve("libs/${project.name}-${project.version}.jar")) // Just a small workaround because it tries to copy the -dev jar if I use: from(tasks.named<Jar>("jar").flatMap { it.archiveFile }.get().asFile)
                        into(buildDir.resolve("MinecraftServer/plugins"))
                    }
                }
            }
        }

        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs += "-Xcontext-receivers"
                jvmTarget = "17"
            }
        }
    }

    java {
        // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}