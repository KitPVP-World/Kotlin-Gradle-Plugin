package world.kitpvp.libraries.paper

import kotlinx.serialization.Serializable

/**
 * "version-build-controller" model for the PaperMC download api v2
 */
@Serializable
data class VersionBuildController(
    val projectId: String,
    val projectName: String,
    val version: String,
    val build: Int,
    val time: String,
    val channel: String,
    val promoted: Boolean,
    val changes: List<Change>,
    val downloads: Downloads,
) {
    @Serializable
    data class Change(
        val commit: String,
        val summary: String,
        val message: String,
    )

    @Serializable
    data class Downloads(
        val application: Download,
        val mojangMappings: Download?,
    ) {
        @Serializable
        data class Download(
            val name: String,
            val sha256: String
        )
    }

}
