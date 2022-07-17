package run.dn5.proxychatsync.configuration

import com.charleskorn.kaml.*
import java.io.File

class ConfigurationLoader(
    private val file: File
) {
    private val config: Configuration = Configuration()

    fun load(): Configuration {
        val nodes = Yaml.default.parseToYamlNode(file.inputStream())
        discord(nodes.yamlMap["discord"])
        save()
        return config
    }

    private fun discord(yamlMap: YamlMap?) {
        if (yamlMap == null) return
        val map = yamlMap.yamlMap
        config.discord.enable = Yamls.getBoolean(map, "enable")
        config.discord.token = Yamls.getString(map, "token") ?: ""
        config.discord.channelId = Yamls.getString(map, "channelId") ?: ""
        config.discord.mode = Yamls.getString(map, "mode") ?: ""
        config.discord.skinImageUrl =
            Yamls.getString(map, "skinImageUrl") ?: "https://crafatar.com/avatars/\${uuid}"
    }

    private fun save() = Yaml.default.encodeToStream(config, file.outputStream())
}