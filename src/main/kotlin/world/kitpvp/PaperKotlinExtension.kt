package world.kitpvp

/**
 * Config for paper kotlin plugin
 */
open class PaperKotlinExtension {
    /**
     * Latest version available at [KSpigot#releases](https://github.com/jakobkmar/KSpigot/releases/latest)
     */
    var kspigotVersion: String = "1.19.2"

    /**
     * Minecraft version
     */
    var minecraftVersion: String = "1.19.4"

    /**
     * plugin.yml api version
     */
    var apiVersion: String = if(minecraftVersion.count { it == '.' } == 1) minecraftVersion else minecraftVersion.substringBeforeLast('.')

    /**
     * Latest build available at [AdvancedSlimePaper#releases](https://github.com/InfernalSuite/AdvancedSlimePaper#releases)
     */
    lateinit var slimeBuild: String

    /**
     * Default: $minecraftVersion-R0.1-SNAPSHOT
     */
    var slimeVersion: String = "$minecraftVersion-R0.1-SNAPSHOT"

    fun isSlimeBuildInitialized() = ::slimeBuild.isInitialized
}