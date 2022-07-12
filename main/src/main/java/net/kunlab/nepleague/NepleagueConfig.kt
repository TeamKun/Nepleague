package net.kunlab.nepleague

import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.collection.TeamSetValue

class NepleagueConfig {
    val rayDistance = DoubleValue(10.0, Double.MIN_VALUE, Double.MAX_VALUE)
    val maxDistance = DoubleValue(10.0, Double.MIN_VALUE, Double.MAX_VALUE)

    val team = TeamSetValue()
}