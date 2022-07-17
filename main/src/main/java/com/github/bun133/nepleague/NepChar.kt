package com.github.bun133.nepleague

class NepChar(
    private var internalChar: Char? = null
) {
    var char: Char?
        get() = this.internalChar
        set(value) {
            this.internalChar = value
        }

    fun isSet() = internalChar != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NepChar

        if (internalChar != other.internalChar) return false

        return true
    }

    override fun hashCode(): Int {
        return internalChar?.hashCode() ?: 0
    }
}