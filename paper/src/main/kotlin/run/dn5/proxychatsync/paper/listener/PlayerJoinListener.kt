package run.dn5.proxychatsync.paper.listener

import com.google.common.io.ByteStreams
import de.myzelyam.api.vanish.VanishAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import run.dn5.proxychatsync.Constants
import run.dn5.proxychatsync.PaperPlugin

class PlayerJoinListener(
    private val plugin: PaperPlugin
): Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent){
        e.joinMessage = null
        if(plugin.useSuperVanish && VanishAPI.isInvisible(e.player)) return

        plugin.logger.info("${e.player.name} joined the server")
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Constants.SUBS2P.PLAYER_JOIN.channel)

        object: BukkitRunnable() {
            override fun run() {
                e.player.sendPluginMessage(plugin, Constants.CHANNEL_FULL, out.toByteArray())
            }
        }.runTaskLater(plugin, 10)
    }
}