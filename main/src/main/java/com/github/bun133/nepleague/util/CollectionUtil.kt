package com.github.bun133.nepleague.util

fun <K : Any, V : Any> MutableMap<K, V>.removeAll(key: K) {
    @Suppress("ControlFlowWithEmptyBody")
    while (this.remove(key) != null) {
    }
}

fun <K : Any, V : Any> MutableMap<K, V>.removeAllValue(value: V) {
    @Suppress("ControlFlowWithEmptyBody")
    while (this.values.remove(value)) {
    }
}