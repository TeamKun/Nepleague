package com.github.bun133.nepleague.command

import com.github.bun133.nepleague.NepleaguePlugin
import dev.kotx.flylib.command.Command
import dev.kotx.flylib.command.arguments.StringArgument

class NepleagueStartCommand : Command("start") {
    init {
        description("Nepleague Start Command")
        usage {
            stringArgument("Answer", StringArgument.Type.PHRASE)
//            stringArgument("Question", StringArgument.Type.PHRASE_QUOTED)

            executes {
                val answer = typedArgs[0] as String

                (plugin as NepleaguePlugin).startWith(answer)

                success("答え: $answer でネプリーグを開始しました")
            }
        }
    }
}