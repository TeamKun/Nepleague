package net.kunlab.nepleague.util

/**
 * 長方形状に整列しているArray
 */
class BoxedArray<T : Any>(val width: Int, val height: Int) {
    private val array = mutableListOf<MutableList<T?>>()

    /**
     * 左下が(0,0)
     * @param x x座標
     * @param y y座標
     */
    operator fun get(x: Int, y: Int): T? {
        if (checkInside(x, y)) {
            return array.getOrNull(y)?.getOrNull(x)
        }
        return null
    }

    operator fun set(x: Int, y: Int, value: T?) {
        if (checkInside(x, y)) {
            array.getOrNull(y)?.set(x, value)
        }
    }

    fun toList(): List<Triple<Int, Int, T>> {
        val list = mutableListOf<Triple<Int, Int, T>>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val value = this[x, y]
                if (value != null) {
                    list.add(Triple(x, y, value))
                }
            }
        }
        return list
    }

    private fun checkInside(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && x < width && y < height
    }
}