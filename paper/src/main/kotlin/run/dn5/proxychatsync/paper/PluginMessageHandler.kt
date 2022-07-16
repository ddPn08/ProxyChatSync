package run.dn5.proxychatsync.paper

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import com.google.gson.GsonBuilder
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import run.dn5.proxychatsync.Constants
import run.dn5.proxychatsync.PaperPlugin
import run.dn5.proxychatsync.model.ChatSyncData
import run.dn5.proxychatsync.model.ServerSwitchData
import java.util.*

class PluginMessageHandler(
    private val plugin: PaperPlugin
): PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if(channel != Constants.CHANNEL_FULL) return
        val input = ByteStreams.newDataInput(message)

        when (input.readUTF()){
            Constants.SUB_P_TO_S.CHAT_SYNC.channel -> chatSync(input)
            Constants.SUB_P_TO_S.SERVER_SWITCH.channel -> serverSwitch(input)
        }
    }

    private fun chatSync(input: ByteArrayDataInput){
        val gson = GsonBuilder().serializeNulls().create()
        val data = input.readUTF()
        val syncData = gson.fromJson(data, ChatSyncData::class.java)
        if(Bukkit.getPlayer(UUID.fromString(syncData.uuid)) != null) return

        var msg = plugin.getMessage().getString("Chat") ?: "<\${prefix}\${author}&r\${suffix}> \${message} &b(\${japanized}) &7@\${server}"

        if(plugin.useLuckPerms){
            val lp = LuckPermsProvider.get()
            val user = lp.userManager.loadUser(UUID.fromString(syncData.uuid)).join()
            if(user != null){
                val meta = user.cachedData.metaData
                if(meta.prefix != null) syncData.prefix = meta.prefix
                if(meta.suffix != null) syncData.suffix = meta.suffix
            }
        }

        msg = msg
            .replace("\${author}", syncData.username ?: "Unknown")
            .replace("\${prefix}", syncData.prefix ?: "")
            .replace("\${suffix}", syncData.suffix ?: "")
            .replace("\${message}", syncData.message ?: "")
            .replace("\${japanized}", syncData.japanized ?: "")
            .replace("\${server}", syncData.server ?: "")
            .replace("<br>", "\n")

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg))
    }
    private fun serverSwitch(input: ByteArrayDataInput){
        val switchData = GsonBuilder().serializeNulls().create().fromJson(input.readUTF(), ServerSwitchData::class.java)
        var msg = plugin.getMessage().getString("ServerJoin") ?: "&6\${player} が &b\${server} &6に参加しました。"

        msg = msg
            .replace("\${player}", switchData.username)
            .replace("\${server}", switchData.from)
            .replace("<br>", "\n")

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg))
    }
}