package run.dn5.proxychatsync.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import run.dn5.proxychatsync.VelocityPlugin

class PostLoginListener(
    private val plugin: VelocityPlugin
) {
    @Subscribe
    fun onPostLogin(e: PostLoginEvent){
        this.plugin.messenger.onLogin(e.player)
    }
}