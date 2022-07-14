package com.github.bun133.nepleague

import com.github.bun133.nepleague.map.MapDisplay
import com.github.bun133.nepleague.map.MapSingleDisplay
import com.github.bun133.nepleague.util.BoxedArray
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ItemFrame
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

    /**
     * 指定LocationからDisplayを選択する
     */
    fun autoSetDisplay(team: Team, location: Location): MapDisplay? {
        val display = selectDisplay(location)
        if (display != null) {
            setDisplay(team, display)
        }
        return display
    }

    private fun selectDisplay(location: Location): MapDisplay? {
        fun selectFrameFilledWithMap(location: Location): ItemFrame? {
            val frame = location.toCenterLocation().getNearbyEntitiesByType(ItemFrame::class.java, 0.5 - 0.0625 + 0.1)
            when (frame.size) {
                0 -> {
                    return null
                }
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
                // 時空が歪んでる
                else -> throw IllegalStateException("This ItemFrame is not facing to the block")
            }

            return mod.map {
                this.location.clone().toBlockLocation()
                    .add(it.first.toDouble(), it.second.toDouble(), it.third.toDouble()).toCenterLocation()
            }
        }

        fun ItemFrame.nextFrame() = nextLocation().mapNotNull { selectFrameFilledWithMap(it) }
        fun ItemFrame.select(concerned: MutableList<ItemFrame> = mutableListOf()): MutableList<ItemFrame> {
            val frames = nextFrame().filter { it !in concerned && it != this }
            return if (frames.isEmpty()) {
                concerned.add(this)
                concerned.distinct().toMutableList()
            } else {
                for (frame in frames) {
                    if (frame in concerned) continue
                    concerned.add(frame)
                    concerned.addAll(frame.select(concerned))
                }
                concerned.distinct().toMutableList()
            }
        }

        fun List<ItemFrame>.toMapDisplay(): MapDisplay {
            if (this.isEmpty()) throw IllegalStateException("There is no ItemFrame")
            fun Int.toIndex(min: Int) = (this - min)
            fun List<ItemFrame>.generateBox(
                width: (ItemFrame) -> Int,
                height: (ItemFrame) -> Int
            ): BoxedArray<ItemFrame> {
                val minW = this.minOfOrNull(width)!!
                val maxW = this.maxOfOrNull(width)!!

                val minH = this.minOfOrNull(height)!!
                val maxH = this.maxOfOrNull(height)!!


                val box = BoxedArray<ItemFrame>(maxW - minW + 1, maxH - minH + 1)
                this.forEach { f ->
                    box[width(f).toIndex(minW), height(f).toIndex(minW)] = f
                }

                return box
            }

            val stackBox = when (this.first().attachedFace) {
                BlockFace.NORTH, BlockFace.SOUTH -> {
                    this.generateBox({
                        it.location.blockX
                    }, {
                        it.location.blockY
                    })
                }
                BlockFace.EAST, BlockFace.WEST -> {
                    this.generateBox({
                        it.location.blockZ
                    }, {
                        it.location.blockY
                    })
                }
                BlockFace.UP, BlockFace.DOWN -> {
                    this.generateBox({
                        it.location.blockX
                    }, {
                        it.location.blockZ
                    })
                }
                // 時空が歪んでる
                else -> throw IllegalStateException("This ItemFrame is not facing to the block")
            }

            return MapDisplay(stackBox.map { MapSingleDisplay(it.item) })
        }

        val frame = selectFrameFilledWithMap(location) ?: return null
        val frames = frame.select()

        println("found ${frames.size} frames while auto-selecting display")
        return frames.toMapDisplay()
    }
}