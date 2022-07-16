package run.dn5.proxychatsync

import com.github.ucchyocean.lc3.LunaChatAPI
import com.github.ucchyocean.lc3.LunaChatBukkit
import net.luckperms.api.LuckPerms
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import run.dn5.proxychatsync.paper.PluginMessageHandler
import run.dn5.proxychatsync.paper.listener.PlayerChatListener
import run.dn5.proxychatsync.paper.listener.PlayerJoinListener
import run.dn5.proxychatsync.paper.listener.PlayerQuitListener
import run.dn5.proxychatsync.paper.listener.PlayerVanishStateChangeListener
import java.io.File
import java.nio.file.Files

class PaperPlugin : JavaPlugin() {

    var useLunaChat = false
    var lunaChatAPI: LunaChatAPI? = null

    var useLuckPerms = false

    var useSuperVanish = false

    override fun onEnable() {
        checkResources()
        checkDepends()
        registerListeners()
        server.messenger.registerIncomingPluginChannel(
            this,
            Constants.CHANNEL_FULL,
            PluginMessageHandler(this)
        );
        server.messenger.registerOutgoingPluginChannel(this, Constants.CHANNEL_FULL);
        logger.info("Enabled")
    }

    private fun registerListeners() {
        val listeners = mutableListOf(
            PlayerChatListener(this),
            PlayerJoinListener(this),
            PlayerQuitListener()
        )
        if (useSuperVanish) listeners.add(PlayerVanishStateChangeListener(this))
        listeners.forEach { server.pluginManager.registerEvents(it, this) }
    }

    private fun checkDepends() {
        if (server.pluginManager.isPluginEnabled("LunaChat")) {
            useLunaChat = true
            lunaChatAPI = (server.pluginManager.getPlugin("LunaChat") as LunaChatBukkit).lunaChatAPI
        }
        if (server.pluginManager.isPluginEnabled("LuckPerms")) {
            useLuckPerms = true
        }
        if (server.pluginManager.isPluginEnabled("SuperVanish")) {
            useSuperVanish = true
        }
    }

    private fun checkResources() {
        if (!dataFolder.exists()) dataFolder.mkdir()
        val file = File("${dataFolder}/message.yml")
        if (!file.exists()) getResource("bukkit.message.yml").use {
            if(it == null) return
            Files.copy(it, file.toPath())
        }
    }

    fun getMessage(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(File("${dataFolder}/message.yml"))
    }
}