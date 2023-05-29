plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
}

group = "world.kitpvp"
version = "1.1.0-SNAPSHOT"
description = "Makes development with advanced slime paper, kspigot, paperweight userdev easier"

val kotlin = "1.8.10"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"
    fun pluginDep(prov: Provider<PluginDependency>): String {
        val pluginDep = prov.get()
        return pluginDep(pluginDep.pluginId, pluginDep.version.displayName)
    }

    implementation(kotlin("gradle-plugin", kotlin)) // Kotlin
    implementation(pluginDep("org.gradle.toolchains.foojay-resolver-convention", "0.5.0")) // Resolving Paperweight
    compileOnly(pluginDep("io.papermc.paperweight.userdev", "1.5.5")) // Paper + NMS
    compileOnly(pluginDep("dev.s7a.gradle.minecraft.server", "2.1.0")) // always choose the latest version from the link above
    compileOnly(pluginDep("net.minecrell.plugin-yml.bukkit", "0.5.3")) // Generates plugin.yml
    compileOnly(pluginDep("cn.apisium.papershelled",  "1.2.1")) // Enable mixin support
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation(gradleApi())
}


gradlePlugin {
    plugins {
        create("paperKotlinProject") {
            id = "world.kitpvp.paper-kotlin-project"
            implementationClass = "world.kitpvp.PaperKotlinProject"
            displayName = "Paper Kotlin Plugin"
            //description = "Makes development with advanced slime paper, kspigot, paperweight userdev easier"
            description = project.description
            tags.set(listOf("minecraft", "paper", "kotlin", "kspigot", "aswm"))
        }

        // I think I don't need to say anything about this
        removeIf {
            it.name.endsWith("dummy")
        }
    }


    vcsUrl.set("https://github.com/KitPVP-World/Paper-Kotlin-Plugin")
    website.set(vcsUrl.get())
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
        maven {
            name = ""
        }
    }
}






java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        // languageVersion: A.B of the kotlin plugin version A.B.C
        languageVersion = kotlin.substringBeforeLast('.')
    }
}

