package com.github.bun133.nepleague

interface StringChecker {
    fun check(char: Char): Boolean
}

enum class StringCheckers(val checker: StringChecker) {
    HIRAGANA(HiraganaChecker()),
    KATAKANA(KatakanaChecker()),
    NONE(NoneChecker())
}

class HiraganaChecker : StringChecker {
    override fun check(char: Char): Boolean {
        return char in '\u3041'..'\u3096' || char == '\u30FC'   // '\u30FC' is 'ー'
    }
}

class KatakanaChecker : StringChecker {
    override fun check(char: Char): Boolean {
        return char in '\u30A1'..'\u30FA' || char == '\u30FC'   // '\u30FC' is 'ー'
    }
}

class NoneChecker : StringChecker {
    override fun check(char: Char): Boolean {
        return true
    }
}

fun StringChecker.check(char: String): Boolean {
    return char.all { this.check(it) }
}
