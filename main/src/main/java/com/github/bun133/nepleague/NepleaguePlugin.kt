package com.github.bun133.nepleague

import dev.kotx.flylib.flyLib
import com.github.bun133.nepleague.command.NepleagueCommand
import org.bukkit.plugin.java.JavaPlugin


class NepleaguePlugin : JavaPlugin() {
    lateinit var config: NepleagueConfig
    lateinit var displayProvider: DisplayProvider
    private var session: NepleagueSession? = null

    fun session() = session
    fun startWith(answer: String) {
        if (session != null) {
            session!!.destroy()
        }
        session = NepleagueSession(this, answer, config.team.value().toList())
        session!!.start()
    }

    override fun onEnable() {
        config = NepleagueConfig(this)
        config.loadConfig()
        config.saveConfigIfAbsent()
        displayProvider = DisplayProvider(config)
        displayProvider.loadFromConfig()

        flyLib {
            command(NepleagueCommand(this@NepleaguePlugin))
        }
    }

    override fun onDisable() {
        config.saveConfigIfPresent()
    }
}