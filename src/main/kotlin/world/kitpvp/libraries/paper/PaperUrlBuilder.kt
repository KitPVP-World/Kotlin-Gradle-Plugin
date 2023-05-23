package world.kitpvp.libraries.paper

import kotlinx.serialization.json.Json
import org.slf4j.Logger
import java.net.URL

object PaperUrlBuilder {
    private const val paperProjectUrl = "https://api.papermc.io/v2/projects/paper"

    /**
     * Returns the download url of a version with the latest build
     * @return /v2/projects/paper/versions/$version/builds/$latestBuild/downlods/$download
     */
    fun getPaperServerUrl(logger: Logger, version: String): String {

        val versionControllerUrl = "$paperProjectUrl/versions/$version"
        logger.debug("Getting latest build version of paper $version - $versionControllerUrl")
        val versionController = Json.decodeFromString<VersionController>(
                URL(versionControllerUrl)
                .readText()
        )
        val latestBuild = versionController.builds.last()
        logger.debug("Latest build of paper $version is $latestBuild")
        val versionBuildControllerUrl = "$versionControllerUrl/builds/$latestBuild"
        logger.debug("Getting file name of the paper $version application build - $latestBuild")
        val versionBuildController = Json.decodeFromString<VersionBuildController>(
            URL(versionBuildControllerUrl)
            .readText()
        )

        val fileName = versionBuildController.downloads.application.name
        logger.debug("File name of the application build $latestBuild is $fileName")
        val downloadUrl = "$versionBuildControllerUrl/downloads/$fileName"
        logger.debug("Latest paper application download url is $downloadUrl")

        return downloadUrl
    }

}