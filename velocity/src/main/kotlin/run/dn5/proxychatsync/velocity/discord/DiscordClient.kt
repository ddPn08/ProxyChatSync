package run.dn5.proxychatsync.velocity.discord

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.restaction.MessageAction
import run.dn5.proxychatsync.configuration.Configuration
import run.dn5.proxychatsync.velocity.Channel
import java.awt.Color
import java.util.*

class DiscordClient(
    private val data: Configuration.Discord
) {
    lateinit var jda: JDA
    private var enabled: Boolean = false
    var webhook: WebhookClient? = null

    fun enable() {
        jda = JDABuilder.createDefault(data.token)
            .setActivity(Activity.listening("ProxyChatSync")).build()
        jda.awaitReady()
        if (data.mode == "webhook") setupWebhook()
        enabled = true
    }

    private fun setupWebhook() {
        val channel = jda.getTextChannelById(data.channelId) ?: throw Exception("Channel not found")

        val wh = channel.retrieveWebhooks().complete().find { it.name == "ProxyChatSync.WebhookMode" }
            ?: channel.createWebhook("ProxyChatSync.WebhookMode").complete()
        webhook = WebhookClientBuilder(wh.url).build()
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun registerEvents(listener: ListenerAdapter) {
        jda.addEventListener(listener)
    }

    fun sendWebhook(message: String, username: String, uuid: String) {
        webhook?.send(
            WebhookMessageBuilder().setUsername(username)
                .setAvatarUrl(data.skinImageUrl.replace("\${uuid}", uuid))
                .setContent(message).build()
        )
    }

    fun sendMessage(message: String, channelId: String = data.channelId) {
        val channel = jda.getTextChannelById(channelId) ?: return
        channel.sendMessage(message).queue()
    }

    fun userAction(message: String?, uuid: UUID, color: Color?) {
        val channel = jda.getTextChannelById(data.channelId) ?: return
        val eb = EmbedBuilder()
            .setAuthor(message, null, data.skinImageUrl.replace("\${uuid}", uuid.toString()))
            .setColor(color)
        channel.sendMessageEmbeds(eb.build()).queue()
    }

    fun getMessageAction(message: String): MessageAction? {
        val channel = jda.getTextChannelById(data.channelId) ?: return null
        return channel.sendMessage(message)
    }

    class MessageListener(
        private val channel: Channel,
        private val client: DiscordClient,
    ) : ListenerAdapter() {
        override fun onMessageReceived(e: MessageReceivedEvent) {
            if (e.channel.id != channel.data.discord.channelId) return
            e.member ?: return
            if (e.author == client.jda.selfUser) return
            channel.chatFromDiscord(e.author, e.message)
        }
    }
}