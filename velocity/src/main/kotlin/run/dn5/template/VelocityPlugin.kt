package run.dn5.template

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(id = BuildConstants.NAME, name = BuildConstants.NAME, version = BuildConstants.VERSION, authors = [BuildConstants.AUTHOR])
class VelocityPlugin @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger,
    @DataDirectory
    private val dataFolder: Path
)  {
    @Subscribe
    fun onEnable(e: ProxyInitializeEvent) {
        this.logger.info("Enabled")
    }
}