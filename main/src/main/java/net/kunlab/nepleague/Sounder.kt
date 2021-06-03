package net.kunlab.nepleague

import org.bukkit.Bukkit
import org.bukkit.Sound

enum class Sounds(private val sound: Sound) {
    // 入力開始時
    OnStart(Sound.BLOCK_ANVIL_STEP),

    // Finishした時のやつ
    OnFinish(Sound.BLOCK_ANVIL_PLACE),

    // 正解のときのやつ
    Correct(Sound.BLOCK_ANVIL_USE),

    // 不正解の時のやつ
    Wrong(Sound.ENTITY_ENDER_DRAGON_AMBIENT);

    fun sound(vol: Float = 5.0f, pitch: Float = 0.0f) {
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(
                net.kyori.adventure.sound.Sound.sound(
                    sound.key,
                    net.kyori.adventure.sound.Sound.Source.MASTER,
                    vol,
                    pitch
                )
            )
        }
    }
}