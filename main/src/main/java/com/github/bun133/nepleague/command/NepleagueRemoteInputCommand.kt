package com.github.bun133.nepleague.command

import dev.kotx.flylib.command.Command
import org.bukkit.Bukkit

class NepleagueRemoteInputCommand : Command("rinput") {
    init {
        description("Input command for remove inputting")
        usage {
            stringArgument("Player name", {
                suggestAll(Bukkit.getServer().onlinePlayers.map { it.name })
            })

            executes {
                val pName = typedArgs[0] as String
                val p = Bukkit.getServer().onlinePlayers.find { it.name == pName }

                if (p != null) {
                    // TODO Remote Inputting
                } else {
                    fail("プレイヤーが見つかりませんでした")
                }
            }
        }
    }
}