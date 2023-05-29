package world.kitpvp

/**
 * Config for paper kotlin plugin
 */
open class KotlinGradleExtension {
    /**
     * Minecraft version
     */
    var minecraftVersion: String = "1.19.4"

    /**
     * plugin.yml api version
     */
    var apiVersion: String = if(minecraftVersion.count { it == '.' } == 1) minecraftVersion else minecraftVersion.substringBeforeLast('.') // Checks if version is e.g. 1.19.4 not 1.19 then removes last digit

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