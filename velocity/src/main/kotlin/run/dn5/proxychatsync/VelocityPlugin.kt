package run.dn5.proxychatsync

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import org.slf4j.Logger
import run.dn5.proxychatsync.velocity.Messenger
import run.dn5.proxychatsync.velocity.discord.listener.MessageListener
import run.dn5.proxychatsync.velocity.listener.DisconnectListener
import run.dn5.proxychatsync.velocity.listener.PluginMessageListener
import run.dn5.proxychatsync.velocity.listener.PostLoginListener
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Plugin(
    id = BuildConstants.ID,
    name = BuildConstants.NAME,
    description = BuildConstants.DESCRIPTION,
    version = BuildConstants.VERSION,
    authors = [BuildConstants.AUTHOR]
)
class VelocityPlugin @Inject constructor(
    val proxy: ProxyServer,
    val logger: Logger,
    @DataDirectory
    val dataFolder: Path
) {

    val common = Common(dataFolder.toFile(), true)
    val messenger = Messenger(this)

    companion object {
        lateinit var instance: VelocityPlugin
    }

    init {
        instance = this
        checkResources()
        messenger.onStart()
        if (common.discordChatSync.enabled) {
            common.discordChatSync.registerEvents(MessageListener(this))
        }
    }

    @Subscribe
    fun onEnable(e: ProxyInitializeEvent) {
        proxy.channelRegistrar.register(
            MinecraftChannelIdentifier.create(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME
            )
        )
        registerListeners()
    }

    @Subscribe
    fun onDisable(e: ProxyShutdownEvent){
        messenger.onStop()
    }

    private fun checkResources() {
        if (!dataFolder.toFile().exists()) dataFolder.toFile().mkdir()
        val file = File("${dataFolder}/message.yml")
        if (!file.exists()) javaClass.getResourceAsStream("/velocity.message.yml").use { input ->
            if (input == null) return
            Files.copy(input, file.toPath())
        }
    }

    private fun registerListeners() {
        listOf(
            PluginMessageListener(this),
            PostLoginListener(this),
            DisconnectListener(this)
        ).forEach { proxy.eventManager.register(this, it) }
    }
}