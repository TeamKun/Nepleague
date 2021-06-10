package net.kunlab.nepleague

import com.github.bun133.flylib2.commands.*
import com.github.bun133.flylib2.utils.ComponentUtils
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.BlockCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class Nepleague : JavaPlugin() {
    var isGoingOn = false
    var isInput = false
    var isFinished = false
    var teamManager = TeamManager(this)
    var configManager = ConfigManger(this)
    var currentString = ""
    var resultMode: ResultMode = ResultMode.Title
    var rightClickWaiter: RightClickWaiter? = null

    enum class ResultMode {
        Title, Chat
    }

    override fun onEnable() {
        saveDefaultConfig()
        configManager.load()

        rightClickWaiter = RightClickWaiter(this)
        val command = Commander(
            this,
            "Nepleague for 50Craft",
            "/nep start|team|config|reset|result add|remove|<configName>|<string>|list|chat|title <teamName>",

            // Start Command

            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
                .addTabChain(TabChain(TabObject("start"), TabPart.EmptySelector()))
                .setInvoker { nepleague, commandSender, strings ->
                    if (!strings[1].matches(Regex("^[\\u3040-\\u309F]+\$"))) {
                        warn("ひらがなを入力してください",commandSender)
                        return@setInvoker false
                    }

                    teamManager.teams.forEach { it.reset() }
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

                    Sounds.OnStart.sound()

                    return@setInvoker true
                },

            // Add Command

            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
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
                        if (teamManager.checkAlreadyExists(strings[2])) {
                            error("その名前のチームは既に登録されています",commandSender)
                            return@setInvoker false
                        }

                        teamManager.addTeam(loc, strings[2])
                        log("Added Team:${strings[2]}",commandSender)
                        return@setInvoker true
                    } else if (commandSender is BlockCommandSender) {
                        val loc = commandSender.block.location
                        if (teamManager.checkAlreadyExists(strings[2])) {
                            error("その名前のチームは既に登録されています",commandSender)
                            return@setInvoker false
                        }

                        teamManager.addTeam(loc, strings[2])
                        log("Added Team:${strings[2]}",commandSender)
                        return@setInvoker true
                    } else {
                        // From Server
                        return@setInvoker false
                    }
                },
            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
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
                        if (teamManager.checkAlreadyExists(strings[2])) {
                            error("その名前のチームは既に登録されています",commandSender)
                            return@setInvoker false
                        }

                        teamManager.addTeam(loc, strings[2], strings[3])
                        log("Added Team:${strings[2]} displayname:${strings[3]}",commandSender)

                        configManager.save()
                        return@setInvoker true
                    } else if (commandSender is BlockCommandSender) {
                        val loc = commandSender.block.location
                        if (teamManager.checkAlreadyExists(strings[2])) {
                            error("その名前のチームは既に登録されています",commandSender)
                        }

                        teamManager.addTeam(loc, strings[2], strings[3])
                        log("Added Team:${strings[2]} displayname:${strings[3]}",commandSender)
                        configManager.save()
                        return@setInvoker true
                    } else {
                        // From Server
                        return@setInvoker false
                    }
                },

            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
                .addTabChain(TabChain(TabObject("team"), TabObject("list")))
                .setInvoker { nepleague, commandSender, strings ->
                    teamManager.teams.forEach {
                        log("InteranalName:${it.internalName},DisplayName:${it.displayName},X:${it.loc.x},Y:${it.loc.y},Z:${it.loc.z}",commandSender)
                    }

                    if (teamManager.teams.isEmpty()) {
                        log("<Empty>")
                    }
                    return@setInvoker true
                },
            // Remove Command

            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
                .addTabChain(TabChain(TabObject("team"), TabObject("remove"), TeamTabObject(this)))
                .setInvoker { nepleague, commandSender, strings ->
                    val teamName = strings[2]
                    if (teamManager.teams.filter { it.internalName == teamName }.isNotEmpty()) {
                        teamManager.teams.removeAll { it.internalName == teamName }
                        log("Removed!")
                        return@setInvoker true
                    }
                    return@setInvoker false
                },

            // Config Command

            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
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
                        TeamTabObject(this),
                        TabPart.EmptySelector(),
                        TabObject(TabPart.selectors, TabPart.playerSelector)
                    )
                )
                .setInvoker { nepleague, commandSender, strings ->
                    val team = teamManager.teams.filter { it.internalName == strings[1] }
                    if (team.size == 1) {
                        val t = team[0]
                        val num = strings[2].toIntOrNull()
                        if (num != null) {
                            val ps = Bukkit.selectEntities(commandSender, strings[3])
                            if (ps.size == 1 && ps[0] is Player) {
                                if (!isGoingOn || isFinished) {
                                    // 晒上げ
//                                    Bukkit.getOnlinePlayers().filter { it != ps[0] }.forEach {
//                                        it.sendMessage("ADHD者:${(ps[0] as Player).displayName}")
//                                    }
                                    warn("まだ始まってません!落ち着いて!",ps[0])
                                    return@setInvoker true
                                }
                                InputWaiter(t, num, ps[0] as Player, this)
                                return@setInvoker true
                            } else {
                                error("プレイヤーが指定されていません",commandSender)
                                return@setInvoker false
                            }
                        } else {
                            error("数字を入力してください",commandSender)
                            return@setInvoker false
                        }
                    } else {
                        println("More than 1 team matched!")
                        error("エラーが発生しました",commandSender)
                        return@setInvoker false
                    }
                },
            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
                .addTabChain(TabChain(TabObject("finish")))
                .setInvoker { nepleague, commandSender, strings ->
                    isFinished = true
                    Sounds.OnFinish.sound()
                    info("締め切り!")
                    return@setInvoker true
                },
            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
                .addTabChain(TabChain(TabObject("result"), TabObject("title")))
                .setInvoker { nepleague, commandSender, strings ->

                    resultMode = ResultMode.Title
                    if (commandSender is Player) {
                        rightClickWaiter!!.players.add(commandSender)
                        info("右クリックでそのチームの結果発表ができるようになりました!",commandSender)
                    }
                    return@setInvoker true
                },
            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
                .addTabChain(TabChain(TabObject("result"), TabObject("chat")))
                .setInvoker { nepleague, commandSender, strings ->
                    resultMode = ResultMode.Chat

                    if (!isFinished) {
                        warn("回答を締め切っていませんよ...")
                        warn("/nep finishですよ...")
                        return@setInvoker true
                    }

                    Sounds.OnStart.sound()

                    info("結果発表!")
                    info("模範解答:${currentString}")
                    Bukkit.broadcastMessage("") // 空白行

                    teamManager.teams
                        // 重複表示回避処理
//                                    .filter { !it.titleProvider.isChated }
                        .forEach { team ->
                            team.titleProvider.isOpened = true
                            team.titleProvider.isChated = true

                            val comps = team.getStringWithPlayer(currentString.length).map {
                                if (it.second != null) {
                                    ComponentUtils.fromText("" + it.first)
                                        .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(it.second!!.displayName()))
                                } else {
                                    ComponentUtils.fromText("" + it.first)
                                        .hoverEvent(
                                            net.kyori.adventure.text.event.HoverEvent.showText(
                                                ComponentUtils.fromText(
                                                    "[無回答]"
                                                )
                                            )
                                        )
                                }
                            }

                            var comp = if (team.isCorrect()) {
                                ComponentUtils.fromText("" + ChatColor.RED + team.displayName + ChatColor.WHITE + ":" + ChatColor.RESET)
                            } else {
                                ComponentUtils.fromText("" + ChatColor.BLUE + team.displayName + ChatColor.WHITE + ":" + ChatColor.RESET)
                            }

                            comps.forEach {
                                comp = comp.append(it)
                            }

                            Bukkit.getOnlinePlayers().forEach { player ->
                                player.sendMessage(comp)
                            }

                            Bukkit.broadcastMessage("") // 空白行
                        }
                    return@setInvoker true
                },
            CommanderBuilder<Nepleague>()
                .addFilter(CommanderBuilder.Filters().filterOp())
//                .addFilter(CommanderBuilder.Filters().filterNotPlayer())
                .addTabChain(TabChain(TabObject("reset")))
                .setInvoker { nepleague, commandSender, strings ->
                    teamManager.teams.forEach {
                        it.reset()
                    }
                    isGoingOn = false
                    isFinished = false
                    isInput = false
                    currentString = ""
                    rightClickWaiter?.players?.clear()
                    info("リセットしました!",commandSender)

                    return@setInvoker true
                }
        )

        command.register("nep")


        /// Title Provider 稼働 /////
        teamManager.runTaskTimer(this, 0, 1)
    }

    override fun onDisable() {
        configManager.save()
    }
}

class TeamTabObject(val plugin: Nepleague) : TabObject() {
    override fun getAsList(): MutableList<String> {
        return plugin.teamManager.teams.map { it.internalName }.toMutableList()
    }
}