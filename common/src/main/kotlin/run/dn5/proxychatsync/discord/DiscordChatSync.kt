package run.dn5.proxychatsync.discord

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.restaction.MessageAction
import run.dn5.proxychatsync.Common
import java.awt.Color
import java.util.*


class DiscordChatSync(
    private val common: Common
) {
    lateinit var jda: JDA
    var enabled: Boolean = false
    var webhook: WebhookClient? = null

    fun enable(token: String) {
        val config = common.getConfig()
        jda = JDABuilder.createDefault(token)
            .setActivity(Activity.listening("ProxyChatSync")).build()
        jda.awaitReady()
        if (config.discord.mode == "webhook") setupWebhook()
        enabled = true
    }

    private fun setupWebhook() {
        val config = common.getConfig()
        val channel = jda.getTextChannelById(config.discord.channelId) ?: throw Exception("Channel not found")

        val wh = channel.retrieveWebhooks().complete().find { it.name == "ProxyChatSync.WebHookMode" }
            ?: channel.createWebhook("ProxyChatSync.WebhookMode").complete()
        webhook = WebhookClientBuilder(wh.url).build()

    }

    fun registerEvents(listener: ListenerAdapter) {
        jda.addEventListener(listener)
    }

    fun sendWebhook(message: String, username: String, uuid: String) {
        webhook?.send(
            WebhookMessageBuilder().setUsername(username)
                .setAvatarUrl(common.getConfig().discord.skinImageUrl.replace("\${uuid}", uuid))
                .setContent(message).build()
        )
    }

    fun sendMessage(message: String, channelId: String = common.getConfig().discord.channelId) {
        val channel = jda.getTextChannelById(channelId) ?: return
        channel.sendMessage(message).queue()
    }

    fun userAction(message: String?, uuid: UUID, color: Color?) {
        val config = common.getConfig()
        val channel = jda.getTextChannelById(config.discord.channelId) ?: return
        val eb = EmbedBuilder()
            .setAuthor(message, null, common.getConfig().discord.skinImageUrl.replace("\${uuid}", uuid.toString()))
            .setColor(color)
        channel.sendMessageEmbeds(eb.build()).queue()
    }

    fun getMessageAction(message: String): MessageAction? {
        val config = common.getConfig()
        val channel = jda.getTextChannelById(config.discord.channelId) ?: return null
        return channel.sendMessage(message)
    }
}