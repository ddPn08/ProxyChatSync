package run.dn5.proxychatsync.velocity

import com.velocitypowered.api.proxy.Player
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader
import run.dn5.proxychatsync.VelocityPlugin
import run.dn5.proxychatsync.model.ChatSyncData
import java.awt.Color
import java.io.File

class Messenger(
    private val plugin: VelocityPlugin
) {
    private fun getMessage(): ConfigurationNode {
        return YAMLConfigurationLoader.builder().setFile(File("${this.plugin.dataFolder}/message.yml")).build().load()
    }
    private fun getMessage(key: String): String? {
        return this.getMessage().getNode(key).string?.replace("<br>", "\n")
    }
    private fun broadcast(msg: String) {
        this.plugin.proxy.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg))
    }

    fun onStart(){
        if(!this.plugin.common.discordChatSync.enabled) return
        this.plugin.common.discordChatSync.sendMessage(this.getMessage("Discord_ServerStart") ?: ":white_check_mark: **サーバーが起動しました**")
    }
    fun onStop(){
        if(!this.plugin.common.discordChatSync.enabled) return
        this.plugin.common.discordChatSync.getMessageAction(this.getMessage("Discord_ServerStop") ?: ":octagonal_sign: **サーバーが停止しました**")?.queue {
            this.plugin.common.discordChatSync.disable()
        }
    }

    fun onDisconnected(player: Player){
        val msg = (this.getMessage("Disconnect") ?: "&e\${player} がサーバーから退出しました。")
            .replace("\${player}", player.username)
        this.broadcast(msg)

        if(this.plugin.common.discordChatSync.enabled){
            val discordMsg = (this.getMessage("Discord_Disconnect") ?: "\${player} がサーバーから退出しました。")
                .replace("\${player}", player.username)
            this.plugin.common.discordChatSync.userAction(discordMsg, player.uniqueId, Color.RED)
        }
    }

    fun onLogin(player: Player){
        val msg = (this.getMessage("ProxyJoin") ?: "&e\${player} がサーバーに参加しました。")
            .replace("\${player}", player.username)
        this.broadcast(msg)

        if(this.plugin.common.discordChatSync.enabled){
            val discordMsg = (this.getMessage("Discord_ProxyJoin") ?: "\${player} がサーバーに参加しました。")
                .replace("\${player}", player.username)
            this.plugin.common.discordChatSync.userAction(discordMsg, player.uniqueId, Color.GREEN)
        }
    }

    fun onServerSwitch(player: Player, server: String){
        if(this.plugin.common.discordChatSync.enabled){
            val discordMsg = (this.getMessage("Discord_ServerSwitch") ?: "\${player} が \${server} に参加しました。")
                .replace("\${player}", player.username)
                .replace("\${server}", server)
            this.plugin.common.discordChatSync.userAction(discordMsg, player.uniqueId, Color.CYAN)
        }
    }

    fun chatToDiscord(syncData: ChatSyncData){
        if(!this.plugin.common.discordChatSync.enabled) return
        val discord = this.plugin.common.discordChatSync
        val message = (
                (if(syncData.japanized == null) this.getMessage("Discord_FromServer") else this.getMessage("Discord_FromServerWithJapanese"))
                    ?: "(\${server}) \${author} > \${message}")
            .replace("\${server}", syncData.server ?: "")
            .replace("\${author}", syncData.username ?: "Unknown")
            .replace("\${message}", syncData.message ?: "")
            .replace("\${japanized}", syncData.japanized ?: "")
        discord.sendMessage(message)
    }
    fun chatFromDiscord(author: User, message: Message){
        val text = (this.getMessage("Discord_FromDiscord") ?: "[ &bDISCORD &r] \${author} &r> \${message}")
            .replace("\${author}", author.name).replace("\${message}", message.contentDisplay)
        this.plugin.proxy.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(text))
    }
}