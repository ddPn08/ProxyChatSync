package run.dn5.proxychatsync.velocity

import com.velocitypowered.api.proxy.server.RegisteredServer
import run.dn5.proxychatsync.VelocityPlugin

class ChannelSet(
    private val plugin: VelocityPlugin,
    private val channels: List<Channel>
) {
    fun forEach(func: (channel: Channel) -> Unit) {
        return channels.forEach(func)
    }

    fun forEachServer(func: (server: RegisteredServer) -> Unit) {
        val servers = channels.flatMap { it.data.servers.toList() }.toSet().toList()
        return servers.forEach { plugin.proxy.getServer(it).ifPresent { server -> func(server) } }
    }
}