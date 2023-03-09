package run.dn5.proxychatsync.velocity

import run.dn5.proxychatsync.VelocityPlugin

class ChannelManager(
    private val plugin: VelocityPlugin
) {

    private val channels = mutableListOf<Channel>()

    fun setup() {
        val config = plugin.getConfig()
        for (x in config.channels) {
            val c = Channel(plugin, x)
            channels.add(c)
        }
    }

    fun getChannels(serverName: String): ChannelSet {
        return ChannelSet(plugin, channels.filter { it.data.servers.contains(serverName) })
    }

    fun onStart() {
        channels.forEach { it.onStart() }
    }

    fun onStop() {
        channels.forEach { it.onStop() }
    }
}