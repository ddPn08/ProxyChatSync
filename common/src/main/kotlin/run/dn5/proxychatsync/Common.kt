package run.dn5.proxychatsync

import com.charleskorn.kaml.Yaml
import run.dn5.proxychatsync.configuration.Configuration
import run.dn5.proxychatsync.configuration.ConfigurationLoader
import run.dn5.proxychatsync.discord.DiscordChatSync
import java.io.File
import java.nio.file.Files

class Common(
    private val dataFolder: File,
    private val isProxy: Boolean
) {
    val discordChatSync: DiscordChatSync = DiscordChatSync(this)

    fun enable() {
        checkResources()
        val config = getConfig()
        if (isProxy && config.discord.enable) discordChatSync.enable(getConfig().discord.token)
    }

    private fun checkResources() {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        if (isProxy) {
            val configFile = File("${dataFolder}/config.yml")
            if (!configFile.exists()) {
                javaClass.getResourceAsStream("/config.yml").use {
                    if (it == null) return
                    Files.copy(it, configFile.toPath())
                }
            }
        }
    }

    fun getConfig(): Configuration {
        return ConfigurationLoader(File("${dataFolder}/config.yml")).load()
    }
}