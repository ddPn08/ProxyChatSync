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
        this.checkResources()
        this.checkDepends()
        this.registerListeners()
        this.server.messenger.registerIncomingPluginChannel(
            this,
            Constants.CHANNEL_FULL,
            PluginMessageHandler(this)
        );
        this.server.messenger.registerOutgoingPluginChannel(this, Constants.CHANNEL_FULL);
        this.logger.info("Enabled")
    }

    private fun registerListeners() {
        val listeners = mutableListOf(
            PlayerChatListener(this),
            PlayerJoinListener(this),
            PlayerQuitListener()
        )
        if (this.useSuperVanish) listeners.add(PlayerVanishStateChangeListener(this))
        listeners.forEach { this.server.pluginManager.registerEvents(it, this) }
    }

    private fun checkDepends() {
        if (this.server.pluginManager.isPluginEnabled("LunaChat")) {
            this.useLunaChat = true
            this.lunaChatAPI = (this.server.pluginManager.getPlugin("LunaChat") as LunaChatBukkit).lunaChatAPI
        }
        if (this.server.pluginManager.isPluginEnabled("LuckPerms")) {
            this.useLuckPerms = true
        }
        if (this.server.pluginManager.isPluginEnabled("SuperVanish")) {
            this.useSuperVanish = true
        }
    }

    private fun checkResources() {
        if (!this.dataFolder.exists()) this.dataFolder.mkdir()
        val file = File("${this.dataFolder}/message.yml")
        if (!file.exists()) this.getResource("bukkit.message.yml").use {
            if(it == null) return
            Files.copy(it, file.toPath())
        }
    }

    fun getMessage(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(File("${this.dataFolder}/message.yml"))
    }
}