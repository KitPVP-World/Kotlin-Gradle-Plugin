package world.kitpvp

import cn.apisium.papershelled.gradle.Extension
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import world.kitpvp.extensions.pluginLibraryHandlerScope
import world.kitpvp.libraries.paper.PaperUrlBuilder
import world.kitpvp.utils.ASWM
import java.io.FileOutputStream

fun Project.applyToProject(extension: KotlinGradleExtension) {
    val pluginLibraryHandlerScope = project.pluginLibraryHandlerScope

    /**
     * Configure Plugins
     */

    // Configure plugin.yml defaults
    if(pluginLibraryHandlerScope.createPluginYml) {
        plugins.apply("net.minecrell.plugin-yml.bukkit") // Generates plugin.yml

        extensions.getByType(BukkitPluginDescription::class).apply {
            apiVersion = extension.apiVersion
            libraries = pluginLibraryHandlerScope.pluginLibraries
            load = BukkitPluginDescription.PluginLoadOrder.STARTUP
            main = "$group.${project.name.lowercase()}.${project.name}"
            description = project.description
            version = project.version.toString()
        }
    }

    // Apply run tasks plugin
    if(pluginLibraryHandlerScope.generateRunTasks) {
        plugins.apply("dev.s7a.gradle.minecraft.server") // Generates run tasks
    }

    // Configure mixin support
    if(pluginLibraryHandlerScope.usingPaperShelled) {
        println("Using paper shelled")

        apply {
            plugin("cn.apisium.papershelled") // Mixin support for paper
        }

        extensions.getByType(Extension::class).apply {
            archiveClassifier.set("mixins")
            jarUrl.set(PaperUrlBuilder.getPaperServerUrl(logger, extension.minecraftVersion))
            reobfAfterJarTask.set(false) // Paperweight should already re-obfuscate it
        }
    }

    /**
     * Dependencies
     */
    repositories {
        mavenCentral()

        if(pluginLibraryHandlerScope.usingPaper)
            maven("https://repo.papermc.io/repository/maven-public/")

        if(pluginLibraryHandlerScope.usingSimpleCloud)
            maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local")

        if(pluginLibraryHandlerScope.usingASWM)
            maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
    }


    dependencies {
        if(pluginLibraryHandlerScope.usingPaperServer) {
            val minecraft = extension.minecraftVersion
            extensions.getByType(PaperweightUserDependenciesExtension::class)
                .paperDevBundle("$minecraft-R0.1-SNAPSHOT")
        }
        for(dependencyNotation in pluginLibraryHandlerScope.libraryImplementations) {
            val splittedNotation = dependencyNotation.split(":")

            val dep = dependencies.create(splittedNotation[0],  splittedNotation[1], splittedNotation[2], null, splittedNotation[3], null)
            dependencies.add("implementation", dep)
        }
        for(dependencyNotation in pluginLibraryHandlerScope.libraryCompileOnlies) {
            val splittedNotation = dependencyNotation.split(":")

            val dep = dependencies.create(splittedNotation[0],  splittedNotation[1], splittedNotation[2], null, splittedNotation[3], null)
            dependencies.add("compileOnly", dep)
        }
    }

    /**
     * Task Configurations
     */
    tasks {
        if(pluginLibraryHandlerScope.usingPaper)
            this.getByName("assemble").dependsOn(named<RemapJar>("reobfJar"))

        /*assemble {
            dependsOn(reobfJar)
        }*/
        

        if(pluginLibraryHandlerScope.generateRunTasks) {
            val taskGroup = "paper-kotlin"
            task<LaunchMinecraftServerTask>("runServer") {
                description = "Runs a slime paper server with the plugin"
                group = taskGroup
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
                group = taskGroup
                description = "Builds the plugin and puts it into build/MinecraftServer/plugins"
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

    extensions.getByType(JavaPluginExtension::class).apply {
        // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}