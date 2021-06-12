package net.kunlab.nepleague

/*
*
*   入力された文字列関係へのUtil
*
*/

// 文字列が有効か
fun isValid(string: String): Boolean {
    return string.toCharArray().all { isValid(it) }
}

// 文字列を変換したうえで有効か
fun isMatchValid(string:String) = isValid(mapToValid(string))

fun isValid(c: Char): Boolean {
    val l = c.code
    if (l in 0x0041..0x005A) {
        // A to Z
        return true
    } else if (l in 0x3041..0x3094) {
        // ぁ to ゔ
        return true
    }
    return false
}

fun isMatchValid(c:Char):Boolean = isValid(mapToValid(c))

// できるだけ有効な文字列に変換する
fun mapToValid(string: String): String {
    val sb = StringBuilder()
    string.toCharArray().forEach {
        sb.append(mapToValid(it))
    }
    return sb.toString()
}

// 有効な文字列に変換する。できなければnull
fun forceMapToValid(string: String): String? {
    val sb = StringBuilder()
    string.toCharArray().forEach {
        val l = forceMapToValid(it) ?: return null
        sb.append(l)
    }
    return sb.toString()
}

// 有効な文字に変換できない場合はそのまま
fun mapToValid(c: Char): Char = forceMapToValid(c) ?: c

// 有効な文字に変換できなければNull
fun forceMapToValid(c: Char): Char? {
    if (isValid(c)) return c
    val l = c.code
    if (l in 0x30A1..0x30F4) {
        // カタカナ
        return Char(l - 0x0060)
    } else if (l in 0x0061..0x007A) {
        // 小文字英語
        return Char(l - 0x0020)
    }

    return null
}