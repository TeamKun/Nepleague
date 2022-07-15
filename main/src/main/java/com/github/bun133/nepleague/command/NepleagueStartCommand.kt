package com.github.bun133.nepleague.command

import com.github.bun133.bukkitfly.component.text
import com.github.bun133.nepleague.NepleaguePlugin
import dev.kotx.flylib.command.Command
import dev.kotx.flylib.command.arguments.StringArgument

class NepleagueStartCommand : Command("start") {
    init {
        description("Nepleague Start Command")
        usage {
            stringArgument("Answer", StringArgument.Type.WORD)
            stringArgument("Question", StringArgument.Type.WORD)

            executes {
                val answer = typedArgs[0] as String
                val question = typedArgs[1] as String

                // TODO : Start Nepleague
                (plugin as NepleaguePlugin).startWith(answer, question)

                success("答え: $answer でネプリーグを開始しました")
            }
        }
    }
}