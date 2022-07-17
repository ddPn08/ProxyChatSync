package run.dn5.proxychatsync.paper.listener

import com.github.ucchyocean.lc3.japanize.JapanizeType
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import run.dn5.proxychatsync.Constants
import run.dn5.proxychatsync.PaperPlugin
import run.dn5.proxychatsync.model.ChatSyncData

class PlayerChatListener(
    private val plugin: PaperPlugin
): Listener {
    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent){
        val player = e.player
        val out = ByteStreams.newDataOutput()

        val data = ChatSyncData(
            player.uniqueId.toString(),
            player.name,
            e.message
        )

        if(plugin.useLunaChat)
            data.japanized = plugin.lunaChatAPI?.japanize(e.message, JapanizeType.GOOGLE_IME).toString()

        out.writeUTF(Constants.SUBS2P.CHAT_SYNC.channel)
        out.writeUTF(Gson().toJson(data))
        player.sendPluginMessage(plugin, Constants.CHANNEL_FULL, out.toByteArray())
    }
}