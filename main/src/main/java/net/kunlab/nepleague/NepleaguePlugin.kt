package net.kunlab.nepleague

import dev.kotx.flylib.flyLib
import net.kunlab.nepleague.command.NepleagueCommand
import org.bukkit.plugin.java.JavaPlugin


class NepleaguePlugin : JavaPlugin() {
    lateinit var config: NepleagueConfig
    override fun onEnable() {
        config = NepleagueConfig(this)
        config.loadConfig()
        config.saveConfigIfAbsent()

        flyLib {
            command(NepleagueCommand(this@NepleaguePlugin))
        }
    }

    override fun onDisable() {
        config.saveConfigIfPresent()
    }
}