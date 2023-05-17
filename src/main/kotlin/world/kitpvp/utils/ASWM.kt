package world.kitpvp.utils


// Minecraft Server Utils
class ASWM {
    companion object {
        fun server(build: String, version: String) : String {
            return "https://dl.rapture.pw/IS/ASP/main/${build}/slimeworldmanager-paperclip-${version}-reobf.jar"
        }
        fun plugin(build: String, version: String) : String {
            return "https://dl.rapture.pw/IS/ASP/main/${build}/plugin-${version}.jar"
        }
    }
}