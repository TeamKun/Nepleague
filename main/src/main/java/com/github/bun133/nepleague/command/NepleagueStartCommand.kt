package com.github.bun133.nepleague.command

import com.github.bun133.nepleague.NepleaguePlugin
import com.github.bun133.nepleague.StringChecker
import com.github.bun133.nepleague.StringCheckers
import com.github.bun133.nepleague.check
import dev.kotx.flylib.command.Command
import dev.kotx.flylib.command.arguments.StringArgument

class NepleagueStartCommand : Command("start") {
    init {
        description("Nepleague Start Command")
        usage {
            stringArgument("Answer Type", StringArgument.Type.WORD, {
                suggestAll(StringCheckers.values().map { it.name })
            })
            stringArgument("Answer", StringArgument.Type.PHRASE)
//            stringArgument("Question", StringArgument.Type.PHRASE_QUOTED)

            executes {
                val answerTypeS = typedArgs[0] as String
                val answerType: StringCheckers
                try {
                    answerType = StringCheckers.valueOf(answerTypeS)
                } catch (e: java.lang.Exception) {
                    fail("Answer Typeが不正です")
                    return@executes
                }

                val answer = typedArgs[1] as String

                if (answerType.checker.check(answer)) {
                    (plugin as NepleaguePlugin).startWith(answer, answerType.checker)
                    success("答え: $answer , Checker:${answerTypeS} でネプリーグを開始しました")
                } else {
                    fail("Answerが不正です")
                }
            }
        }
    }
}