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
    fun onPluginMessage(e: PluginMessageEvent) {
        val input = ByteStreams.newDataInput(e.data)
        val sub = input.readUTF()
        val connection = e.source as ServerConnection

        when (sub) {
            Constants.SUBS2P.CHAT_SYNC.channel -> chatSync(input, connection)
            Constants.SUBS2P.PLAYER_JOIN.channel -> playerJoin(connection)
            Constants.SUBS2P.VANISH_PLAYER_HIDE.channel -> vanishPlayerHide(connection)
            Constants.SUBS2P.VANISH_PLAYER_SHOW.channel -> vanishPlayerShow(connection)
        }
    }

    private fun chatSync(input: ByteArrayDataInput, connection: ServerConnection) {
        val gson = GsonBuilder().serializeNulls().create()
        val data = input.readUTF()
        val syncData = gson.fromJson(data, ChatSyncData::class.java)
        syncData.server = connection.serverInfo.name

        val out = ByteStreams.newDataOutput()
        out.writeUTF(Constants.SUBP2S.CHAT_SYNC.channel)
        out.writeUTF(gson.toJson(syncData))
        plugin.proxy.allServers.filter { it.playersConnected.isNotEmpty() && it.serverInfo != connection.serverInfo }
            .forEach {
                it.sendPluginMessage(
                    MinecraftChannelIdentifier.create(Constants.CHANNEL_ID, Constants.CHANNEL_NAME),
                    out.toByteArray()
                )
            }

        plugin.messenger.chatToDiscord(syncData)
    }

    private fun playerJoin(connection: ServerConnection) {
        val player = connection.player

        val out = ByteStreams.newDataOutput()
        out.writeUTF(Constants.SUBP2S.SERVER_SWITCH.channel)
        out.writeUTF(
            Gson().toJson(
                ServerSwitchData(
                    player.uniqueId.toString(),
                    player.username,
                    connection.serverInfo.name
                )
            )
        )

        plugin.proxy.allServers.filter { it.playersConnected.isNotEmpty() }.forEach {
            it.sendPluginMessage(
                MinecraftChannelIdentifier.create(Constants.CHANNEL_ID, Constants.CHANNEL_NAME),
                out.toByteArray()
            )
        }

        plugin.messenger.onServerSwitch(player, connection.serverInfo.name)
    }

    private fun vanishPlayerHide(connection: ServerConnection) {
        plugin.messenger.onDisconnected(connection.player)
    }

    private fun vanishPlayerShow(connection: ServerConnection) {
        val player = connection.player
        plugin.messenger.onLogin(player)

        playerJoin(connection)
    }


}