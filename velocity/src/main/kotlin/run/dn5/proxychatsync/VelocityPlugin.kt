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
import run.dn5.proxychatsync.configuration.Configuration
import run.dn5.proxychatsync.configuration.ConfigurationLoader
import run.dn5.proxychatsync.velocity.Channel
import run.dn5.proxychatsync.velocity.ChannelManager
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

    //    val common = Common(dataFolder.toFile(), true)
//    val messenger = Channel(this)
    private lateinit var config: Configuration
    val channelManger = ChannelManager(this)

    companion object {
        lateinit var instance: VelocityPlugin
    }

    init {
        instance = this
        checkResources()
    }

    @Subscribe
    fun onEnable(e: ProxyInitializeEvent) {
        proxy.channelRegistrar.register(
            MinecraftChannelIdentifier.create(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME
            )
        )
        
        loadConfig()
//        common.enable()
        registerListeners()
        channelManger.setup()
        channelManger.onStart()

//        val ds = common.discordChatSync
//        if (ds.enabled) {
//            ds.registerEvents(MessageListener(this))
//            messenger.onStart()
//        }
    }

    @Subscribe
    fun onDisable(e: ProxyShutdownEvent) {
        channelManger.onStop()
    }

    private fun checkResources() {
        if (!dataFolder.toFile().exists()) dataFolder.toFile().mkdir()
        val file = File("${dataFolder}/message.yml")
        if (!file.exists()) javaClass.getResourceAsStream("/velocity.message.yml").use { input ->
            if (input != null) Files.copy(input, file.toPath())
        }
        val configFile = File("${dataFolder}/config.yml")
        if (!configFile.exists()) javaClass.getResourceAsStream("/config.yml").use {
            if (it != null) Files.copy(it, configFile.toPath())
        }
    }

    private fun loadConfig() {
        val configFile = File("${dataFolder}/config.yml")
        val loader = ConfigurationLoader(configFile)
        config = loader.load()
    }

    private fun registerListeners() {
        listOf(
            PluginMessageListener(this),
            PostLoginListener(this),
            DisconnectListener(this)
        ).forEach { proxy.eventManager.register(this, it) }
    }

    fun getConfig(): Configuration {
        return config
    }
}