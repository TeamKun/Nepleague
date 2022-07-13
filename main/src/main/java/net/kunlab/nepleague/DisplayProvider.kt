package net.kunlab.nepleague

import net.kunlab.nepleague.map.MapDisplay
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Team

class DisplayProvider(private val conf: NepleagueConfig) {
    private val displays = mutableMapOf<Team, MapDisplay>()

    fun getDisplay(team: Team) = displays[team]

    /**
     * Set [display] to [team]
     * if the [display] is already owned by other team, it will be transferred from the other team.
     */
    fun setDisplay(team: Team, display: MapDisplay) {
        if (displays.containsValue(display)) {
            val key = displays.filter { it.value == display }.keys.first()
            if (key != team) {
                displays.remove(key)
                displays[team] = display
            } else {
                // do nothing
            }
        } else {
            displays[team] = display
        }
    }

    fun loadFromConfig() {
        conf.displays.value().forEach { (team, location) ->
            val display = selectDisplay(location)
            if (display != null) {
                setDisplay(team, display)
            }
        }
    }

    fun selectDisplay(location: Location): MapDisplay? {
        fun selectFrameFilledWithMap(location: Location): ItemFrame? {
            val frame = location.toCenterLocation().getNearbyEntitiesByType(ItemFrame::class.java, 0.5 - 0.225)
            when (frame.size) {
                0 -> return null
                1 -> {
                    val item = frame.first().item
                    if (item.type == Material.FILLED_MAP) {
                        return frame.first()
                    }
                    return null
                }
                else -> {
                    // 時空が歪んでる
                    throw IllegalStateException("There are multiple ItemFrames at the same location")
                }
            }
        }

        fun ItemFrame.nextLocation(): List<Location> {
            val mod = when (this.attachedFace) {
                BlockFace.NORTH, BlockFace.SOUTH -> listOf(
                    Triple(0, 1, 0),
                    Triple(0, -1, 0),
                    Triple(1, 0, 0),
                    Triple(-1, 0, 0)
                )
                BlockFace.EAST, BlockFace.WEST -> listOf(
                    Triple(0, 1, 0),
                    Triple(0, -1, 0),
                    Triple(0, 0, 1),
                    Triple(0, 0, -1)
                )
                BlockFace.UP, BlockFace.DOWN -> listOf(
                    Triple(1, 0, 0),
                    Triple(-1, 0, 0),
                    Triple(0, 0, 1),
                    Triple(0, 0, -1)
                )
                BlockFace.NORTH_EAST,
                BlockFace.NORTH_WEST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH_WEST,
                BlockFace.WEST_NORTH_WEST,
                BlockFace.NORTH_NORTH_WEST,
                BlockFace.NORTH_NORTH_EAST,
                BlockFace.EAST_NORTH_EAST,
                BlockFace.EAST_SOUTH_EAST,
                BlockFace.SOUTH_SOUTH_EAST,
                BlockFace.SOUTH_SOUTH_WEST,
                BlockFace.WEST_SOUTH_WEST,
                BlockFace.SELF -> throw IllegalStateException("This ItemFrame is not facing to the block")
            }

            return mod.map {
                this.location.clone().toBlockLocation()
                    .add(it.first.toDouble(), it.second.toDouble(), it.third.toDouble()).toCenterLocation()
            }
        }

        // TODO マップの自動選択
    }
}