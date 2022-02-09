package run.dn5.template

import org.bukkit.plugin.java.JavaPlugin

class PaperPlugin: JavaPlugin() {
    override fun onEnable() {
        this.logger.info("Enabled")
    }
}