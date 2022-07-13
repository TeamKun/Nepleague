package net.kunlab.nepleague.command

import dev.kotx.flylib.command.Command
import net.kunlab.nepleague.NepleaguePlugin
import net.kunmc.lab.configlib.ConfigCommandBuilder

class NepleagueCommand(plugin: NepleaguePlugin) : Command("nep") {
    init {
        description("Nepleague Command")
        children(
            ConfigCommandBuilder(plugin.config).build(),
            NepleagueStartCommand(),
        )
    }
}