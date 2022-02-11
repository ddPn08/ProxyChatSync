package run.dn5.proxychatsync

import kotlinx.serialization.Serializable

@Serializable
data class Configuration (
    val discord: Discord
) {
    @Serializable
    data class Discord(
        val enable: Boolean,
        val token: String,
        val channelId: String
    )
}
