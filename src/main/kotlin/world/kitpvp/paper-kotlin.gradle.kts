package world.kitpvp

import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import gradle.kotlin.dsl.accessors._46d043dc7409397208137acfa1647ba6.bukkit
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.gradle.api.GradleException
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import world.kitpvp.extensions.pluginLibraries
import world.kitpvp.extensions.pluginLibraryImplementation
import world.kitpvp.extensions.usingSimpleCloud
import world.kitpvp.utils.ASWM
import java.io.FileOutputStream

private val extension = project.extensions
    .create("paperKotlin", PaperKotlinExtension::class.java)

plugins {
    kotlin("jvm")
    `java-library`
    id("io.papermc.paperweight.userdev")
    id("dev.s7a.gradle.minecraft.server") // always choose the latest version from the link above
    id("net.minecrell.plugin-yml.bukkit") // Generates plugin.yml
}

repositories {
    mavenCentral()
    maven("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
}

dependencies {
    val kspigot = extension.kspigotVersion
    val minecraft = extension.minecraftVersion

    paperweight.paperDevBundle("$minecraft-R0.1-SNAPSHOT")
    pluginLibraryImplementation("net.axay:kspigot:$kspigot")
}

// Configure plugin.yml defaults
bukkit {
    apiVersion = extension.apiVersion
    libraries = pluginLibraries
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "$group.${project.name.lowercase()}.${project.name}"
    description = project.description
    version = project.version.toString()
}

// Kotlin / Java Optimizations
kotlin {
    jvmToolchain(17)
}

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
            /*copy {
                val file = tasks.named<AbstractArchiveTask>("shadowJar").flatMap { shadow -> shadow.archiveFile }.get().asFile
                from(file)
                into(buildDir.resolve("MinecraftServer/plugins"))
            }*/


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