package com.github.bun133.nepleague.util

/**
 * 長方形状に整列しているArray
 */
class BoxedArray<T : Any>(val width: Int, val height: Int) {
    private val array = mutableMapOf<Int, MutableMap<Int, T>>()

    private fun getY(y: Int): MutableMap<Int, T>? {
        if (checkInside(0, y)) {
            val e = array[y]
            if (e == null) {
                array[y] = mutableMapOf()
                return array[y]
            }
            return e
        } else {
            return null
        }
    }

    /**
     * 左下が(0,0)
     * @param x x座標
     * @param y y座標
     */
    operator fun get(x: Int, y: Int): T? {
        if (checkInside(x, y)) {
            return getY(y)!![x]
        }
        return null
    }

    operator fun set(x: Int, y: Int, value: T?) {
        if (checkInside(x, y)) {
            if (value != null) {
                getY(y)!![x] = value
            } else {
                getY(y)!!.remove(x)
            }
        } else {
            throw IndexOutOfBoundsException("($x,$y)")
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

    fun <U : Any> map(f: (T) -> U): BoxedArray<U> {
        val new = BoxedArray<U>(width, height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val value = this[x, y]
                if (value != null) {
                    new[x, y] = f(value)
                }
            }
        }

        return new
    }
}