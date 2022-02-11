package run.dn5.proxychatsync.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.restaction.MessageAction
import run.dn5.proxychatsync.Common
import java.awt.Color
import java.util.*


class DiscordChatSync(
    private val common: Common
) {
    var enabled: Boolean = false
    private var jda: JDA? = null

    fun enable(token: String){
        this.jda = JDABuilder.createDefault(token).build()
        this.jda!!.awaitReady()
        this.enabled = true
    }

    fun disable(){
        if(!this.enabled) throw Exception("DiscordChatSync is not enabled")
        this.enabled = false
        jda!!.cancelRequests()
        jda!!.shutdownNow()
    }

    fun registerEvents(listener: ListenerAdapter){
        this.getClient().addEventListener(listener)
    }

    private fun getClient(): JDA {
        if(this.jda == null) throw Exception("Discord client not enabled")
        return this.jda!!
    }

    fun getOwn(): User {
        return this.getClient().selfUser
    }
    fun sendMessage(message: String, channelId: String = this.common.getConfig().discord.channelId) {
        val channel = this.getClient().getTextChannelById(channelId) ?: return
        channel.sendMessage(message).queue()
    }
    fun userAction(message: String?, uuid: UUID, color: Color?) {
        val eb = EmbedBuilder()
        val config = this.common.getConfig()
        eb.setAuthor(message, null, "https://crafatar.com/avatars/${uuid}")
        eb.setColor(color)
        sendEmbed(eb.build(), config.discord.channelId)
    }

    fun getMessageAction(message: String): MessageAction? {
        val config = this.common.getConfig()
        val channel = this.getClient().getTextChannelById(config.discord.channelId) ?: return null
        return channel.sendMessage(message)
    }

    fun sendEmbed(embed: MessageEmbed, channelId: String) {
        val channel = this.getClient().getTextChannelById(channelId) ?: return
        channel.sendMessageEmbeds(embed).queue()
    }

}