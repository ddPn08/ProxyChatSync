package run.dn5.proxychatsync.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import run.dn5.proxychatsync.VelocityPlugin

class DisconnectListener(
    private val plugin: VelocityPlugin
) {
    @Subscribe
    fun onDisconnect(e: DisconnectEvent) {
        e.player.currentServer.ifPresent { server ->
            val channels = plugin.channelManger.getChannels(server.serverInfo.name)
            channels.forEach { it.onDisconnected(e.player) }
        }
    }
}