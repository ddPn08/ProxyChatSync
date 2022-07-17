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
        return YAMLConfigurationLoader.builder().setFile(File("${plugin.dataFolder}/message.yml")).build().load()
    }

    private fun getMessage(key: String): String? {
        return getMessage().getNode(key).string?.replace("<br>", "\n")
    }

    private fun broadcast(msg: String) {
        plugin.proxy.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg))
    }

    fun onStart() {
        if (!plugin.common.discordChatSync.enabled) return
        plugin.common.discordChatSync.sendMessage(
            getMessage("Discord_ServerStart") ?: ":white_check_mark: **サーバーが起動しました**"
        )
    }

    fun onStop() {
        if (!plugin.common.discordChatSync.enabled) return
        try {
            plugin.common.discordChatSync.getMessageAction(
                getMessage("Discord_ServerStop") ?: ":octagonal_sign: **サーバーが停止しました**"
            )?.complete()
        } catch (_: Exception) {}
    }

    fun onDisconnected(player: Player) {
        val msg = (getMessage("Disconnect") ?: "&e\${player} がサーバーから退出しました。")
            .replace("\${player}", player.username)
        broadcast(msg)

        if (plugin.common.discordChatSync.enabled) {
            val discordMsg = (getMessage("Discord_Disconnect") ?: "\${player} がサーバーから退出しました。")
                .replace("\${player}", player.username)
            plugin.common.discordChatSync.userAction(discordMsg, player.uniqueId, Color.RED)
        }
    }

    fun onLogin(player: Player) {
        val msg = (getMessage("ProxyJoin") ?: "&e\${player} がサーバーに参加しました。")
            .replace("\${player}", player.username)
        broadcast(msg)

        if (plugin.common.discordChatSync.enabled) {
            val discordMsg = (getMessage("Discord_ProxyJoin") ?: "\${player} がサーバーに参加しました。")
                .replace("\${player}", player.username)
            plugin.common.discordChatSync.userAction(discordMsg, player.uniqueId, Color.GREEN)
        }
    }

    fun onServerSwitch(player: Player, server: String) {
        if (plugin.common.discordChatSync.enabled) {
            val discordMsg = (getMessage("Discord_ServerSwitch") ?: "\${player} が \${server} に参加しました。")
                .replace("\${player}", player.username)
                .replace("\${server}", server)
            plugin.common.discordChatSync.userAction(discordMsg, player.uniqueId, Color.CYAN)
        }
    }

    fun chatToDiscord(syncData: ChatSyncData) {
        if (!plugin.common.discordChatSync.enabled) return
        val discord = plugin.common.discordChatSync
        val message = (
                (if (syncData.japanized.isNotEmpty()) getMessage("Discord_FromServer") else getMessage("Discord_FromServerWithJapanese"))
                    ?: "(\${server}) \${author} > \${message}")
            .replace("\${server}", syncData.server)
            .replace("\${author}", syncData.username.ifEmpty { "Unknown" })
            .replace("\${message}", syncData.message)
            .replace("\${japanized}", syncData.japanized)

        if (discord.webhook != null) discord.sendWebhook(syncData.message, syncData.username, syncData.uuid)
        else discord.sendMessage(message)

    }

    fun chatFromDiscord(author: User, message: Message) {
        val text = (getMessage("Discord_FromDiscord") ?: "[ &bDISCORD &r] \${author} &r> \${message}")
            .replace("\${author}", author.name).replace("\${message}", message.contentDisplay)
        plugin.proxy.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(text))
    }
}