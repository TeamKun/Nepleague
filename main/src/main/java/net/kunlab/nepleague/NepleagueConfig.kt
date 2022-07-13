package net.kunlab.nepleague

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.collection.TeamSetValue
import net.kunmc.lab.configlib.value.map.Team2LocationMapValue

class NepleagueConfig(plugin: NepleaguePlugin) : BaseConfig(plugin) {
    val rayDistance = DoubleValue(10.0, Double.MIN_VALUE, Double.MAX_VALUE)
    val maxDistance = DoubleValue(10.0, Double.MIN_VALUE, Double.MAX_VALUE)
    val team = TeamSetValue()

    val displays = Team2LocationMapValue()
}