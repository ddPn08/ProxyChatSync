package run.dn5.proxychatsync

import com.charleskorn.kaml.Yaml
import run.dn5.proxychatsync.discord.DiscordChatSync
import java.io.File
import java.nio.file.Files

class Common(
    private val dataFolder: File,
    private val isProxy: Boolean
) {
    val discordChatSync: DiscordChatSync = DiscordChatSync(this)

    init {
        this.checkResources()
        if (this.isProxy && this.getConfig().discord.enable) this.discordChatSync.enable(this.getConfig().discord.token)
    }

    private fun checkResources() {
        if (!this.dataFolder.exists()) this.dataFolder.mkdirs()
        if(this.isProxy){
            val configFile = File("${this.dataFolder}/config.yml")
            if (!configFile.exists()) {
                this.javaClass.getResourceAsStream("/config.yml").use {
                    if (it == null) return
                    Files.copy(it, configFile.toPath())
                }
            }
        }
    }

    fun getConfig(): Configuration {
        return Yaml.default.decodeFromStream(
            Configuration.serializer(),
            File("${this.dataFolder}/config.yml").inputStream()
        )
    }
}