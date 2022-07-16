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
        jda = JDABuilder.createDefault(token).build()
        jda!!.awaitReady()
        enabled = true
    }

    fun disable(){
        if(!enabled) throw Exception("DiscordChatSync is not enabled")
        enabled = false
        jda!!.cancelRequests()
        jda!!.shutdownNow()
    }

    fun registerEvents(listener: ListenerAdapter){
        getClient().addEventListener(listener)
    }

    private fun getClient(): JDA {
        if(jda == null) throw Exception("Discord client not enabled")
        return jda!!
    }

    fun getOwn(): User {
        return getClient().selfUser
    }
    fun sendMessage(message: String, channelId: String = common.getConfig().discord.channelId) {
        val channel = getClient().getTextChannelById(channelId) ?: return
        channel.sendMessage(message).queue()
    }
    fun userAction(message: String?, uuid: UUID, color: Color?) {
        val eb = EmbedBuilder()
        val config = common.getConfig()
        eb.setAuthor(message, null, "https://crafatar.com/avatars/${uuid}")
        eb.setColor(color)
        sendEmbed(eb.build(), config.discord.channelId)
    }

    fun getMessageAction(message: String): MessageAction? {
        val config = common.getConfig()
        val channel = getClient().getTextChannelById(config.discord.channelId) ?: return null
        return channel.sendMessage(message)
    }

    fun sendEmbed(embed: MessageEmbed, channelId: String) {
        val channel = getClient().getTextChannelById(channelId) ?: return
        channel.sendMessageEmbeds(embed).queue()
    }

}