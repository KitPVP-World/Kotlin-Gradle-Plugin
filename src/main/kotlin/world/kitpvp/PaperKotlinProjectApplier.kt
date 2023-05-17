package world.kitpvp

import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import gradle.kotlin.dsl.accessors._46d043dc7409397208137acfa1647ba6.*
import gradle.kotlin.dsl.accessors._46d043dc7409397208137acfa1647ba6.paperweight
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import world.kitpvp.extensions.pluginLibraries
import world.kitpvp.extensions.pluginLibraryImplementation
import world.kitpvp.extensions.usingSimpleCloud
import world.kitpvp.utils.ASWM
import java.io.FileOutputStream

val applyToProject: (Project.(
    extension: PaperKotlinExtension, pluginYml: Boolean, runTasks: Boolean, paperShelled: Boolean) -> Unit) = {
    extension: PaperKotlinExtension, pluginYml: Boolean, runTasks: Boolean, paperShelled: Boolean ->

    /**
     * Configure Plugins
     */

    // Configure plugin.yml defaults
    if(pluginYml) {
        bukkit {
            apiVersion = extension.apiVersion
            libraries = pluginLibraries
            load = BukkitPluginDescription.PluginLoadOrder.STARTUP
            main = "$group.${project.name.lowercase()}.${project.name}"
            description = project.description
            version = project.version.toString()
        }

    }
    // Configure mixin support
    /*if(paperShelled) {
        paperShelled {
            archiveClassifier = "Hallo"
            jarUrl = ""
        }
    }*/

    // Kotlin / Java Optimizations
    kotlin {
        jvmToolchain(17)
    }

    /**
     * Dependencies
     */
    repositories {
        mavenCentral()
        maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
        maven("https://maven.fabricmc.net/")
    }

    dependencies {
        val kspigot = extension.kspigotVersion
        val minecraft = extension.minecraftVersion

        paperweight.paperDevBundle("$minecraft-R0.1-SNAPSHOT")
        pluginLibraryImplementation("net.axay:kspigot:$kspigot")
    }

    /**
     * Task Configurations
     */
    tasks {
        // Configure reobfJar to run when invoking the build task
        assemble {
            dependsOn(reobfJar)
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

            // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
            // See https://openjdk.java.net/jeps/247 for more information.
            options.release.set(17)
        }
        javadoc {
            options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        }
        processResources {
            filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        }

        if(runTasks) {
            // Minecraft Server
            task<LaunchMinecraftServerTask>("runServer") {
                dependsOn("reloadPlugin")

                val slimeVersion by lazy { extension.slimeVersion }
                val slimeBuild by lazy { extension.slimeBuild }
                var ready = true

                // dependsOn("shadowJar")
                doFirst {
                    if(!ready) {
                        throw GradleException("No slime specified! Server won't start.")
                    }
                    if(usingSimpleCloud)
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