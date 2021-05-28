package net.kunlab.nepleague

import org.bukkit.ChatColor
import org.bukkit.Location

class ConfigManger(val plugin: Nepleague) {

    init {
        load()
    }

    var rayDistance: Double = 10.0
    var maxDistance: Double = 10.0

    fun load() {
        rayDistance = plugin.config.getDouble("RayDistance")
        maxDistance = plugin.config.getDouble("MaxDistance")

        plugin.teamManager.teams = mutableListOf()
        plugin.config.getConfigurationSection("Teams")?.getKeys(false)?.forEach {
            val loc = getLoc("Teams.$it.Pos")
            val displayName = plugin.config.getString("Teams.$it.DisplayName")

            if (loc != null && displayName != null) {
                plugin.teamManager.addTeam(
                    loc,
                    it,
                    displayName
                )
            } else {
                println("loc or displayName is null")
                println("loc:$loc")
                println("displayName:$displayName")
            }
        }
    }

    fun save() {
        plugin.config.set("RayDistance", rayDistance)
        plugin.config.set("MaxDistance", maxDistance)
        plugin.teamManager.teams.forEach {
            println("Saving:Teams.${it.internalName}.DisplayName")
            plugin.config.set("Teams.${it.internalName}.DisplayName", it.displayName)
            saveLoc("Teams.${it.internalName}.Pos", it.loc)
        }
        println("Saving Config")
        plugin.saveConfig()
    }

    fun saveLoc(path: String, loc: Location) {
        plugin.config.set("$path.X", loc.x)
        plugin.config.set("$path.Y", loc.y)
        plugin.config.set("$path.Z", loc.z)
        plugin.config.set("$path.World", loc.world.name)
    }

    fun getLoc(path: String): Location? {
        val x = plugin.config.get("$path.X")
        val y = plugin.config.get("$path.Y")
        val z = plugin.config.get("$path.Z")
        val world = plugin.config.get("$path.World")

        if (x !is Double || y !is Double || z !is Double || world !is String) {
            return null
        }
        val w = plugin.server.getWorld(world)
        if (w == null) {
            plugin.server.broadcastMessage("" + ChatColor.RED + "ワールドを取得できませんでした(reloadすると直る可能性があります)")
            println("WorldNull")
            return null
        }

        return Location(w, x, y, z)
    }
}