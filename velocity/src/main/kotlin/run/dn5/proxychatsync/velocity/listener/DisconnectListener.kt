package run.dn5.proxychatsync.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import run.dn5.proxychatsync.VelocityPlugin

class DisconnectListener(
    private val plugin: VelocityPlugin
) {
    @Subscribe
    fun onDisconnect(e: DisconnectEvent) {
        this.plugin.messenger.onDisconnected(e.player)
    }
}