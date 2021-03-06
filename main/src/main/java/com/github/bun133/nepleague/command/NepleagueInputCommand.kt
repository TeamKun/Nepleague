package com.github.bun133.nepleague.command

import com.github.bun133.nepleague.NepChar
import com.github.bun133.nepleague.NepleaguePlugin
import dev.kotx.flylib.command.Command
import dev.kotx.flylib.command.arguments.StringArgument
import org.bukkit.Bukkit

class NepleagueInputCommand : Command("input") {
    init {
        description("Input Command of Nepleague Game")
        usage {
            integerArgument("Index(0 indexed)")
            stringArgument("Input", StringArgument.Type.PHRASE)
            executes {
                val p = player
                if (p != null) {
                    val s = typedArgs[1] as String
                    if (s.length == 1) {
                        val c = s.toCharArray()[0]
                        val session = (plugin as NepleaguePlugin).session()
                        if (session != null) {
                            val index = typedArgs[0] as Int
                            val team =
                                Bukkit.getServer().scoreboardManager.mainScoreboard.teams.find { it.entries.contains(p.name) }
                            if (team != null) {
                                if (session.joinedTeam.contains(team)) {
                                    val b = session.setInput(team, index, NepChar(c))
                                    if (b) {
                                        success("回答しました")
                                    } else {
                                        fail("問題の指示に従った回答ではありません (ひらがななどの指定がありませんか?)")
                                    }
                                } else {
                                    fail("あなたのチームはゲームに参加していません")
                                }
                            } else {
                                fail("チームに参加していません")
                            }
                        } else {
                            fail("ゲームが開始されていません")
                        }
                    } else {
                        fail("一文字で入力してください")
                    }
                } else {
                    fail("プレイヤーから実行してください")
                }
            }
        }
    }
}