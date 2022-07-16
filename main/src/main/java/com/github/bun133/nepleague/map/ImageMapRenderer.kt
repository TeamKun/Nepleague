package com.github.bun133.nepleague.map

import org.bukkit.entity.Player
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.image.BufferedImage
import kotlin.reflect.KProperty

class ImageMapRenderer() : MapRenderer() {
    var buf: BufferedImage = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        repeat(canvas.cursors.size()) {
            canvas.cursors.removeCursor(canvas.cursors.getCursor(0))
        }

        canvas.drawImage(0, 0, buf)
    }

    object Delegate {
        class ImageMapRender(val map: MapSingleDisplay) {
            operator fun getValue(map: MapSingleDisplay, property: KProperty<*>): ImageMapRenderer? {
                val meta = map.stack.itemMeta ?: return null
                val mapMeta = meta as? MapMeta ?: return null
                val view = mapMeta.mapView ?: return null
                view.renderers.filter { it !is ImageMapRenderer }.toList().forEach { view.removeRenderer(it) }
                val f = view.renderers.filterIsInstance<ImageMapRenderer>()
                return if (f.isNotEmpty()) {
                    f.first()
                } else {
                    val r = ImageMapRenderer()
                    view.addRenderer(r)
                    r
                }
            }
        }
    }
}