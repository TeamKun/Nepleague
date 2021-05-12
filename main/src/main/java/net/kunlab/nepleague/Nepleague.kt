package net.kunlab.nepleague

import com.github.bun133.flylib2.commands.*
import com.github.bun133.flylib2.utils.ComponentUtils
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


class Nepleague : JavaPlugin() {
    var isGoingOn = false
    var isInput = false
    var isFinished = false
    var teamManager = TeamManager(this)
    var configManager = ConfigManger(this)
    var currentString = ""


    override fun onEnable() {
        saveDefaultConfig()
        val command = Commander(
            this, "Nepleague for 50Craft", "/nep start|team|config add|remove|<configName>",

            // Start Command

            CommanderBuilder<Nepleague>()
                .addTabChain(TabChain(TabObject("start"), TabPart.EmptySelector()))
                .setInvoker { nepleague, commandSender, strings ->
                    if (!strings[1].matches(Regex("^[\\u3040-\\u309F]+\$"))) {
                        commandSender.sendMessage("ひらがなを入力してください")
                        return@setInvoker false
                    }

                    currentString = strings[1]
                    isGoingOn = true
                    isInput = true
                    isFinished = false
                    Bukkit.getOnlinePlayers().forEach {
                        it.showTitle(
                            Title.title(
                                ComponentUtils.fromText("50人ネプリーグ開始!"),
                                ComponentUtils.fromText("")
                            )
                        )
                    }
                    return@setInvoker true
                },

            // Add Command

            CommanderBuilder<Nepleague>()
                .addTabChain(
                    TabChain(
                        TabObject("team"),
                        TabObject("add"),
                        TabPart.EmptySelector()
                    )
                )
                .setInvoker { nepleague, commandSender, strings ->
                    if (commandSender is Player) {
                        val loc = commandSender.location
                        if(teamManager.checkAlreadyExists(strings[2])){
                            commandSender.sendMessage("その名前のチームは既に登録されています")
                            return@setInvoker false
                        }

                        teamManager.addTeam(loc, strings[2])
                        commandSender.sendMessage("Added Team:${strings[2]}")
                        return@setInvoker true
                    } else if (commandSender is BlockCommandSender) {
                        val loc = commandSender.block.location
                        if(teamManager.checkAlreadyExists(strings[2])){
                            commandSender.sendMessage("その名前のチームは既に登録されています")
                            return@setInvoker false
                        }

                        teamManager.addTeam(loc, strings[2])
                        commandSender.sendMessage("Added Team:${strings[2]}")
                        return@setInvoker true
                    } else {
                        // From Server
                        return@setInvoker false
                    }
                },
            CommanderBuilder<Nepleague>()
                .addTabChain(
                    TabChain(
                        TabObject("team"),
                        TabObject("add"),
                        TabPart.EmptySelector(),
                        TabPart.EmptySelector()
                    )
                )
                .setInvoker { nepleague, commandSender, strings ->
                    if (commandSender is Player) {
                        val loc = commandSender.location
                        if(teamManager.checkAlreadyExists(strings[2])){
                            commandSender.sendMessage("その名前のチームは既に登録されています")
                            return@setInvoker false
                        }

                        teamManager.addTeam(loc, strings[2], strings[3])
                        commandSender.sendMessage("Added Team:${strings[2]} displayname:${strings[3]}")
                        return@setInvoker true
                    } else if (commandSender is BlockCommandSender) {
                        val loc = commandSender.block.location
                        if(teamManager.checkAlreadyExists(strings[2])){
                            commandSender.sendMessage("その名前のチームは既に登録されています")
                            return@setInvoker false
                        }

                        teamManager.addTeam(loc, strings[2], strings[3])
                        commandSender.sendMessage("Added Team:${strings[2]} displayname:${strings[3]}")
                        return@setInvoker true
                    } else {
                        // From Server
                        return@setInvoker false
                    }
                },

            CommanderBuilder<Nepleague>()
                .addTabChain(TabChain(TabObject("team"), TabObject("list")))
                .setInvoker { nepleague, commandSender, strings ->
                    teamManager.teams.forEach {
                        commandSender.sendMessage("InteranalName:${it.internalName},DisplayName:${it.displayName},X:${it.loc.x},Y:${it.loc.y},Z:${it.loc.z}")
                    }

                    if (teamManager.teams.isEmpty()) {
                        commandSender.sendMessage("<Empty>")
                    }
                    return@setInvoker true
                },
            // Remove Command

            CommanderBuilder<Nepleague>()
                .addTabChain(TabChain(TabObject("team"), TabObject("remove"), TabPart.EmptySelector()))
                .setInvoker { nepleague, commandSender, strings ->
                    val teamName = strings[2]
                    if (teamManager.teams.filter { it.internalName == teamName }.isNotEmpty()) {
                        teamManager.teams.removeAll { it.internalName == teamName }
                        commandSender.sendMessage("Removed!")
                        return@setInvoker true
                    }
                    return@setInvoker false
                },

            // Config Command

            CommanderBuilder<Nepleague>()
                .addTabChain(TabChain(TabObject("config"), TabObject("RayDistance", "MaxDistance")))
                .setInvoker { nepleague, commandSender, strings ->
                    when (strings[1]) {
                        "RayDistance" -> {
                            val d = strings[0].toDoubleOrNull()
                            if (d == null) {
                                return@setInvoker false
                            } else {
                                configManager.rayDistance = d
                            }
                        }

                        "MaxDistance" -> {
                            val d = strings[0].toDoubleOrNull()
                            if (d == null) {
                                return@setInvoker false
                            } else {
                                configManager.maxDistance = d
                            }
                        }

                        else -> {
                            return@setInvoker false
                        }
                    }
                    configManager.save()
                    return@setInvoker true
                },

            // Input Command

            CommanderBuilder<Nepleague>()
                .addTabChain(
                    TabChain(
                        TabObject("input"),
                        // TODO 起動時にしか生成されなくてどうにもならない
                        TabObject(*(teamManager.teams.map { it.internalName }.toTypedArray())),
                        TabPart.EmptySelector(),
                        TabObject(TabPart.selectors, TabPart.playerSelector)
                    )
                )
                .setInvoker { nepleague, commandSender, strings ->
                    if (!isGoingOn) return@setInvoker false
                    val team = teamManager.teams.filter { it.internalName == strings[1] }
                    if (team.size == 1) {
                        val t = team[0]
                        val num = strings[2].toIntOrNull()
                        if (num != null) {
                            val ps = Bukkit.selectEntities(commandSender, strings[3])
                            if (ps.size == 1 && ps[0] is Player) {
                                InputWaiter(t, num, ps[0] as Player, this)
                                return@setInvoker true
                            } else {
                                commandSender.sendMessage("プレイヤーが指定されていません")
                                return@setInvoker false
                            }
                        } else {
                            commandSender.sendMessage("数字を入力してください")
                            return@setInvoker false
                        }
                    } else {
                        println("More than 1 team matched!")
                        commandSender.sendMessage("エラーが発生しました")
                        return@setInvoker false
                    }
                },
            CommanderBuilder<Nepleague>()
                .addTabChain(TabChain(TabObject("finish")))
                .setInvoker { nepleague, commandSender, strings ->
                    isFinished = true
                    Bukkit.getOnlinePlayers().forEach {
                        it.showTitle(
                            Title.title(
                                ComponentUtils.fromText("締め切り!"),
                                ComponentUtils.fromText("")
                            )
                        )
                    }
                    return@setInvoker true
                },
            CommanderBuilder<Nepleague>()
                .addTabChain(TabChain(TabObject("result"), TabObject("title")))
                .setInvoker { nepleague, commandSender, strings ->
                    if (commandSender is Player) {
                        val title = TitleProvider.getProvider(commandSender, this)
                        if (title != null) {
                            // TODO 演出
                            title.isOpened = true
                        } else {
                            commandSender.sendMessage("チームが未選択です")
                        }
                        return@setInvoker true
                    }
                    return@setInvoker false
                },
            CommanderBuilder<Nepleague>()
                .addTabChain(TabChain(TabObject("result"), TabObject("chat")))
                .setInvoker { nepleague, commandSender, strings ->
                    if (commandSender is Player) {
                        val title = TitleProvider.getProvider(commandSender, this)
                        if (title != null) {
                            // TODO 演出
                            title.isOpened = true
                        } else {
                            commandSender.sendMessage("チームが未選択です")
                        }
                        return@setInvoker true
                    }
                    return@setInvoker false
                }
        )

        command.register("nep")
    }

    override fun onDisable() {
        configManager.save()
    }
}

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
            val loc = plugin.config.getLocation("Teams.$it.Pos")
            val displayName = plugin.config.getString("Teams.$it.DisplayName")

            if (loc != null && displayName != null) {
                plugin.teamManager.addTeam(
                    loc,
                    it,
                    displayName
                )
            }
        }
    }

    fun save() {
        plugin.config.set("RayDistance", rayDistance)
        plugin.config.set("MaxDistance", maxDistance)
        plugin.teamManager.teams.forEach {
            plugin.config.set("Teams.${it.internalName}.DisplayName", it.displayName)
            plugin.config.set("Teams.${it.internalName}.Pos", it.loc)
        }
        plugin.saveConfig()
    }
}

class TeamManager(val plugin: Nepleague) : BukkitRunnable() {
    var teams = mutableListOf<Team>()
    fun addTeam(loc: Location, internalName: String) {
        addTeam(loc, internalName, "チーム$internalName")
    }

    fun addTeam(loc: Location, internalName: String, displayName: String) {
        teams.add(Team(loc, internalName, displayName, plugin))
    }

    fun checkAlreadyExists(internalName: String): Boolean {
        return teams.filter { it.internalName == internalName }.isNotEmpty()
    }

    // Showing Titles
    override fun run() {
        if (plugin.isGoingOn) {
            Bukkit.getOnlinePlayers().forEach {
                TitleProvider.getProvider(it, plugin.configManager.rayDistance, plugin.configManager.maxDistance)
                    ?.showTo(it)
            }
        }
    }
}

class Team(var loc: Location, val internalName: String, var displayName: String, val plugin: Nepleague) {
    var answers = mutableMapOf<Int, Pair<Player, Char>>()
    var titleProvider: TitleProvider = TitleProvider(this, plugin = plugin)
    fun getPlayers(): List<Player> {
        return answers.map { it.value.first }
    }

    fun set(index: Int, p: Player, s: Char) {
        answers[index] = Pair(p, s)
    }

    fun getString(size: Int): String {
        val s = StringBuilder()
        for (i in 0 until size) {
            val d = answers.mapValues { it.value.second }.getOrDefault(i, "□")
            s[i] = d as Char
        }
        if (!titleProvider.isOpened) return s.toString()
        else {
            for (i in 0 until size) {
                if (s[i] != '□') {
                    s[i] = '■'
                }
            }
            return s.toString()
        }
    }

    fun getPlayerAnswer(p: Player): Pair<Player, Char>? {
        return answers.filterValues { it.first === p }[0]
    }

    fun getAnswerIndex(ans: Pair<Player, Char>): Int? {
        val anss = answers.map { entry -> Pair(entry.key, entry.value) }.filter { it.second === ans }
        return if (anss.isNotEmpty()) {
            anss[0].first
        } else {
            null
        }
    }
}

class TitleProvider(val team: Team, var size: Int = 0, val plugin: Nepleague) {
    init {
        providers.add(this)
    }

    var isOpened = false

    companion object {
        val providers = mutableListOf<TitleProvider>()
        fun getProvider(player: Player, plugin: Nepleague): TitleProvider? {
            return getProvider(player, plugin.configManager.rayDistance, plugin.configManager.maxDistance)
        }

        fun getProvider(player: Player, rayDistance: Double, maxDistance: Double): TitleProvider? {
            if (providers.map { it.team }.any { it.getPlayers().contains(player) }) {
                // playerがどこかのチームに所属している場合
                val ps = providers.filter { it.team.getPlayers().contains(player) }
                if (ps.size == 1) {
                    return ps[0]
                } else {
                    println("ERROR:In getProvider,Player(${ComponentUtils.toText(player.displayName())}) must answered at two position!")
                    player.sendMessage("ERROR:In getProvider,You must answered at two position!")
                    player.sendMessage("" + ChatColor.RED + "2つ以上の場所で回答しないでください!")
                }
            } else {
                // playerがどこかのチームに所属していない場合
                val block = player.rayTraceBlocks(rayDistance)
                if (block != null && block.hitBlock != null) {
                    val distance = providers.map { Pair(it, it.team.loc.distance(block.hitBlock!!.location)) }
                        .filter { it.second <= maxDistance }.minByOrNull { it.second }
                    if (distance != null) {
                        return distance.first
                    } else {
                        // ブロックにhitしたが範囲内にTeamがいないとき(非表示)
                    }
                } else {
                    // どのブロックにもhitしないとき(非表示)
                }
            }
            return null
        }
    }

    fun showTo(p: Player) {
        // TODO
        if (team.getPlayers().contains(p)) {
            //チーム内
            val s = team.getString(size)
            val an = team.getPlayerAnswer(p)
            if (an == null) {
                p.showTitle(
                    Title.title(
                        ComponentUtils.fromText(s),
                        ComponentUtils.fromText(team.displayName)
                    )
                )
            } else {
                val index = team.getAnswerIndex(an)
                if (index != null) {
                    val sb = StringBuilder(s)
                    sb[index] = an.second
                    p.showTitle(
                        Title.title(
                            ComponentUtils.fromText(sb.toString()),
                            ComponentUtils.fromText(team.displayName)
                        )
                    )
                } else {
                    println("ERROR:In TitleProvider.showTo(player(${ComponentUtils.toText(p.displayName())}))")
                }
            }
        } else {
            p.showTitle(
                Title.title(
                    ComponentUtils.fromText(team.getString(size)),
                    ComponentUtils.fromText(team.displayName)
                )
            )
        }
    }
}

class InputWaiter(val team: Team, val index: Int, val player: Player, val plugin: Nepleague) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        player.sendMessage("1文字ひらがなを入力してください")
    }

    var isAlready = false

    @EventHandler
    fun onChat(e: AsyncChatEvent) {
        if (e.player === player && !isAlready && !plugin.isFinished) {
            val s = ComponentUtils.toText(e.message())
            if (s.length == 1 && s.matches(Regex("^[\\u3040-\\u309F]+\$"))) {
                e.isCancelled = true
                team.set(index, player, s[0])
                isAlready = true
            } else {
                player.sendMessage("1文字ひらがなを入力してください")
            }
        }
    }
}