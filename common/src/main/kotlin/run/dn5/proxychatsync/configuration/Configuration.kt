package run.dn5.proxychatsync.configuration

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    var channels: List<Channel>
) {
    @Serializable
    data class Channel(
        var discord: Discord,
        var servers: List<String>
    )

    @Serializable
    data class Discord(
        var enable: Boolean = false,
        var token: String = "",
        var channelId: String = "",
        var mode: String = "bot",
        var skinImageUrl: String = ""
    )
}
