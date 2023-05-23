package world.kitpvp.libraries.paper

import kotlinx.serialization.Serializable

/**
 * "version-controller" model for the PaperMC download api v2
 */
@Serializable
data class VersionController(
    val projectId: String,
    val projectName: String,
    val version: String,
    val builds: List<Int>,
)
