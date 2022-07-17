package run.dn5.proxychatsync.paper.listener

import com.google.common.io.ByteStreams
import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import run.dn5.proxychatsync.Constants
import run.dn5.proxychatsync.PaperPlugin

class PlayerVanishStateChangeListener(
    private val plugin: PaperPlugin
): Listener {
    @EventHandler
    fun onPlayerVanishStateChange(e: PlayerVanishStateChangeEvent){
        val player = Bukkit.getPlayer(e.uuid) ?: return
        val out = ByteStreams.newDataOutput()
        if(e.isVanishing) {
            out.writeUTF(Constants.SUBS2P.VANISH_PLAYER_HIDE.channel)
        } else {
            out.writeUTF(Constants.SUBS2P.VANISH_PLAYER_SHOW.channel)
        }
        out.writeUTF(e.uuid.toString())
        player.sendPluginMessage(plugin, Constants.CHANNEL_FULL, out.toByteArray())
    }
}