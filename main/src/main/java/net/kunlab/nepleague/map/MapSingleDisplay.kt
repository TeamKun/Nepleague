package net.kunlab.nepleague.map

import com.github.bun133.bukkitfly.stack.DelegatedItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import java.awt.Graphics2D

class MapSingleDisplay(val stack: ItemStack) : Display {
    constructor(world: World) : this(generateMapStack(world))

    companion object {
        private fun generateMapStack(world: World): ItemStack {
            val map = Bukkit.getServer().createMap(world)
            map.renderers.toList().forEach { map.removeRenderer(it) }
            val stack = ItemStack(Material.FILLED_MAP)
            val meta = stack.itemMeta as MapMeta
            meta.mapView = map
            return stack
        }
    }

    val mapRenderer: ImageMapRenderer? by ImageMapRenderer.Delegate.ImageMapRender(this)

    override fun flush(f: (Graphics2D) -> Unit) {
        if (mapRenderer != null) {
            val g = mapRenderer!!.buf.graphics as Graphics2D
            f(g)
            g.dispose()
        } else {
            throw IllegalStateException("#MapSingleDisplay stack is not a map")
        }
    }
}