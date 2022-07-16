package run.dn5.proxychatsync.velocity.discord.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import run.dn5.proxychatsync.VelocityPlugin

class MessageListener(
    private val plugin: VelocityPlugin
): ListenerAdapter() {
    override fun onMessageReceived(e: MessageReceivedEvent) {
        val config = plugin.common.getConfig()
        if(e.channel.id != config.discord.channelId) return
        e.member ?: return
        if(e.author == plugin.common.discordChatSync.getOwn()) return
        plugin.messenger.chatFromDiscord(e.author, e.message)
    }
}