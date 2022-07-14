package com.github.bun133.nepleague.command

import dev.kotx.flylib.command.Command
import com.github.bun133.nepleague.NepleaguePlugin
import net.kunmc.lab.configlib.ConfigCommandBuilder

class NepleagueCommand(plugin: NepleaguePlugin) : Command("nep") {
    init {
        description("Nepleague Command")
        children(
            ConfigCommandBuilder(plugin.config).build(),
            NepleagueStartCommand(),
            NepleagueDisplayAddCommand()
        )
    }
}