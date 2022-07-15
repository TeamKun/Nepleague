package com.github.bun133.nepleague.command

import com.github.bun133.bukkitfly.component.text
import com.github.bun133.nepleague.NepleaguePlugin
import dev.kotx.flylib.command.Command
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit

class NepleagueRemoteInputCommand : Command("rinput") {
    init {
        description("Input command for remove inputting")
        usage {
            stringArgument("Player name", {
                suggestAll(Bukkit.getServer().onlinePlayers.map { it.name })
            })
            integerArgument("Index")

            executes {
                val pName = typedArgs[0] as String
                val p = Bukkit.getServer().onlinePlayers.find { it.name == pName }
                val index = typedArgs[1] as Int

                if (p != null) {
                    val session = (plugin as NepleaguePlugin).session()
                    if (session != null) {
                        session.remoteInput(p, index)
                        success("Remote Inputを開始しました")
                        p.sendMessage(text("回答を入力してください[${index + 1}文字目]", NamedTextColor.GREEN))
                    } else {
                        fail("ゲームが開始されていません")
                    }
                } else {
                    fail("プレイヤーが見つかりませんでした")
                }
            }
        }
    }
}