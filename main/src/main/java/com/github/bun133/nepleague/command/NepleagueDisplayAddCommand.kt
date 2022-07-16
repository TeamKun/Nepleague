package com.github.bun133.nepleague.command

import com.github.bun133.nepleague.NepleaguePlugin
import dev.kotx.flylib.command.Command
import org.bukkit.Bukkit
import org.bukkit.Location

class NepleagueDisplayAddCommand : Command("display") {
    init {
        description("Add(or set) DisplayCommand")
        usage {
            stringArgument("TeamName", {
                suggestAll((plugin as NepleaguePlugin).config.team.value().map { it.name })
            })
            integerArgument("Index")
            locationArgument("DisplayLocation")

            executes {
                val teamS = typedArgs[0] as String
                val team = Bukkit.getServer().scoreboardManager.mainScoreboard.teams.find { it.name == teamS }
                val index = typedArgs[1] as Int
                if (team == null) {
                    fail("チームが見つかりません")
                } else {
                    if (player != null) {
                        val loc = (typedArgs[2] as Location).also {
                            it.world = player!!.location.world
                        }
                        plugin as NepleaguePlugin
                        val display = (plugin as NepleaguePlugin).displayProvider.autoSetDisplay(team, index, loc)
                        if (display != null) {
                            success("チーム${team.name}の${index + 1}つ目のDisplayを設定しました(サイズ W:${display.maps.width} H:${display.maps.height})")
                            if (display.maps.width != display.maps.height) {
                                fail("[WARN] Displayが正方形ではありません")
                            }
                        } else {
                            fail("Displayを設定できませんでした")
                        }
                    } else {
                        fail("プレイヤーから実行してください")
                    }
                }
            }
        }
    }
}