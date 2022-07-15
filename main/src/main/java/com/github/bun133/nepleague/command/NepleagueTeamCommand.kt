package com.github.bun133.nepleague.command

import com.github.bun133.nepleague.NepleaguePlugin
import dev.kotx.flylib.command.Command
import org.bukkit.Bukkit

/**
 * ConfigCommandからいじれるけどわかりづらいと思うので
 */
class NepleagueTeamCommand : Command("team") {
    init {
        description("Team Command for Nepleague")
        usage {
            selectionArgument("Operation", "list","clear")
            executes {
                when (val operation = typedArgs[0] as String) {
                    "list" -> {
                        val conf = (plugin as NepleaguePlugin).config
                        success("Team:[${conf.team.value().joinToString(", ") { it.name }}]")
                    }
                    "clear" -> {
                        (plugin as NepleaguePlugin).config.team.clear()
                        success("Team cleared")
                    }
                    else -> {
                        fail("Unknown operation: $operation")
                    }
                }
            }
        }

        children(TeamAddCommand(), TeamRemoveCommand())
    }
}

class TeamAddCommand : Command("add") {
    init {
        description("add Team Command")
        usage {
            stringArgument("Team Name", {
                suggestAll(Bukkit.getServer().scoreboardManager.mainScoreboard.teams.map { it.name })
            })

            executes {
                val teamS = typedArgs[0] as String
                val team = Bukkit.getServer().scoreboardManager.mainScoreboard.teams.find { it.name == teamS }

                if (team != null) {
                    val conf = (plugin as NepleaguePlugin).config
                    conf.team.add(team)
                    success("チームを追加しました")
                } else {
                    fail("チームが見つかりませんでした")
                }
            }
        }
    }
}

class TeamRemoveCommand : Command("remove") {
    init {
        description("remove Team Command")
        usage {
            stringArgument("Team Name", {
                val conf = (plugin as NepleaguePlugin).config
                suggestAll(conf.team.value().map { it.name })
            })

            executes {
                val teamS = typedArgs[0] as String
                val team = Bukkit.getServer().scoreboardManager.mainScoreboard.teams.find { it.name == teamS }

                if (team != null) {
                    val conf = (plugin as NepleaguePlugin).config
                    conf.team.remove(team)
                    success("チームを削除しました")
                } else {
                    fail("チームが見つかりませんでした")
                }
            }
        }
    }
}