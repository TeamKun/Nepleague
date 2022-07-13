package net.kunlab.nepleague

class NepChar(
    private var internalChar: Char? = null
) {
    var char: Char?
        get() = this.internalChar
        set(value) {
            this.internalChar = value
        }

    fun isSet() = internalChar != null

    fun prettyPrint(): Char {
        return if (isSet()) {
            internalChar!!
        } else {
            ' ' // TODO Make this configurable
        }
    }

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