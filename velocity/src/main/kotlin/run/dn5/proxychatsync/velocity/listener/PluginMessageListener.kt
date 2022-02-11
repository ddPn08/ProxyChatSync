package run.dn5.proxychatsync.velocity.listener

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import run.dn5.proxychatsync.Constants
import run.dn5.proxychatsync.VelocityPlugin
import run.dn5.proxychatsync.model.ChatSyncData
import run.dn5.proxychatsync.model.ServerSwitchData

class PluginMessageListener(
    private val plugin: VelocityPlugin
) {
    @Subscribe
    fun onPluginMessage(e: PluginMessageEvent){
        val input = ByteStreams.newDataInput(e.data)
        val sub = input.readUTF()
        val connection = e.source as ServerConnection

        this.plugin.logger.info(sub)

        when (sub){

            Constants.SUB_S_TO_P.CHAT_SYNC.channel -> this.chatSync(input, connection)
            Constants.SUB_S_TO_P.PLAYER_JOIN.channel -> this.playerJoin(input, connection)
            Constants.SUB_S_TO_P.VANISH_PLAYER_HIDE.channel -> this.vanishPlayerHide(input, connection)
            Constants.SUB_S_TO_P.VANISH_PLAYER_SHOW.channel -> this.vanishPlayerShow(input, connection)
        }
    }

    private fun chatSync(input: ByteArrayDataInput, connection: ServerConnection){
        val gson = GsonBuilder().serializeNulls().create()
        val data = input.readUTF()
        val syncData = gson.fromJson(data, ChatSyncData::class.java)
        syncData.server = connection.serverInfo.name

        val out = ByteStreams.newDataOutput()
        out.writeUTF(Constants.SUB_P_TO_S.CHAT_SYNC.channel)
        out.writeUTF(gson.toJson(syncData))
        this.plugin.proxy.allServers.forEach {
            it.sendPluginMessage(MinecraftChannelIdentifier.create(Constants.CHANNEL_ID, Constants.CHANNEL_NAME), out.toByteArray())
        }

        this.plugin.messenger.chatToDiscord(syncData)
    }

    private fun playerJoin(input: ByteArrayDataInput, connection: ServerConnection){
        val player = connection.player

        val out = ByteStreams.newDataOutput()
        out.writeUTF(Constants.SUB_P_TO_S.SERVER_SWITCH.channel)
        out.writeUTF(Gson().toJson(ServerSwitchData(player.uniqueId.toString(), player.username, connection.serverInfo.name)))

        for (s in this.plugin.proxy.allServers) {
            if(s.playersConnected.isEmpty()) continue
            s.sendPluginMessage(MinecraftChannelIdentifier.create(Constants.CHANNEL_ID, Constants.CHANNEL_NAME), out.toByteArray())
        }

        this.plugin.messenger.onServerSwitch(player, connection.serverInfo.name)
    }

    private fun vanishPlayerHide(input: ByteArrayDataInput, connection: ServerConnection){
        this.plugin.messenger.onDisconnected(connection.player)
    }

    private fun vanishPlayerShow(input: ByteArrayDataInput, connection: ServerConnection){
        val player = connection.player
        this.plugin.messenger.onLogin(player)

        val out = ByteStreams.newDataOutput()
        out.writeUTF(Constants.SUB_P_TO_S.SERVER_SWITCH.channel)
        out.writeUTF(Gson().toJson(ServerSwitchData(player.uniqueId.toString(), player.username, connection.serverInfo.name)))

        for (s in this.plugin.proxy.allServers) {
            if(s.playersConnected.isEmpty()) continue
            s.sendPluginMessage(MinecraftChannelIdentifier.create(Constants.CHANNEL_ID, Constants.CHANNEL_NAME), out.toByteArray())
        }
    }


}