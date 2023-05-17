package world.kitpvp

/**
 * idk why this file needs to be here but if it isn't the build will miss accessors of the following plugins and fail the build.
 */
plugins {
    kotlin("jvm")
    `java-library`
    id("io.papermc.paperweight.userdev")
    id("dev.s7a.gradle.minecraft.server") // always choose the latest version from the link above
    id("net.minecrell.plugin-yml.bukkit") // Generates plugin.yml
}
