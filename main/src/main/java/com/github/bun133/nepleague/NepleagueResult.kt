package com.github.bun133.nepleague

import org.bukkit.scoreboard.Team

enum class NepleagueResult(val displayString: String) {
    Correct("正解"),
    Wrong("不正解"),
    NoInput("未入力")
}

fun judgeInput(answer: String, input: Array<NepChar>): List<NepleagueResult>? {
    if (answer.length != input.size) {
        return null
    } else {
        return input.mapIndexed { index, nepChar ->
            val c = nepChar.char
            if (c != null) {
                val b = answer[index] == c
                if (b) {
                    NepleagueResult.Correct
                } else {
                    NepleagueResult.Wrong
                }
            } else {
                NepleagueResult.NoInput
            }
        }
    }
}

fun judgeInput(
    answer: String,
    all: List<Pair<Team, Array<NepChar>>>
): List<Pair<Team, Pair<List<NepleagueResult>?, NepleagueResult>>> {
    fun mapResult(result: List<NepleagueResult>?): NepleagueResult {
        if (result == null) {
            // おかしいぽ
            throw IllegalArgumentException("result is null")
        } else {
            return if (result.any { it == NepleagueResult.Wrong }) {
                NepleagueResult.Wrong
            } else if (result.any { it == NepleagueResult.NoInput }) {
                NepleagueResult.NoInput
            } else {
                NepleagueResult.Correct
            }
        }
    }

    val judges = all.map { (team, input) ->
        val result = judgeInput(answer, input)
        team to (result to mapResult(result))
    }

    return judges
}