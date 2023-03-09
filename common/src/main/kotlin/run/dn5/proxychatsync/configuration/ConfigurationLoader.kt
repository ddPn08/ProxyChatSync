package run.dn5.proxychatsync.configuration

import com.charleskorn.kaml.*
import java.io.File

class ConfigurationLoader(
    private val file: File
) {

    private lateinit var config: Configuration

    fun load(): Configuration {
        config = Yaml.default.decodeFromStream(Configuration.serializer(), file.inputStream())
        return config
    }

    private fun save() = Yaml.default.encodeToStream(config, file.outputStream())
}