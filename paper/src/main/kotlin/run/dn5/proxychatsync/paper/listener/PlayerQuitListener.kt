package run.dn5.proxychatsync.paper.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener: Listener {
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent){
        e.quitMessage = null
    }
}