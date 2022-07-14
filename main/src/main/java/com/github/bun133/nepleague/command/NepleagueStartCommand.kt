package com.github.bun133.nepleague.command

import dev.kotx.flylib.command.Command

class NepleagueStartCommand : Command("start") {
    init {
        description("Nepleague Start Command")
        usage {
            stringArgument("Answer")

            executes {
                val answer = typedArgs[0] as String

                // TODO : Start Nepleague

                success("お題: $answer でネプリーグを開始しました")
            }
        }
    }
}