package run.dn5.proxychatsync.configuration

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    var discord: Discord = Discord(),
) {
    @Serializable
    data class Discord(
        var enable: Boolean = false,
        var token: String = "",
        var channelId: String = "",
        var mode: String = "bot",
        var skinImageUrl: String = ""
    )
}
