package dev.wintu.mho

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack

const val LIMIT = 60 * 30

class Core: Listener {
    private val players = Bukkit.getOnlinePlayers()
    private var currentTime = LIMIT
    private val currentTask = Bukkit.getScheduler().runTaskTimer(Mho.instance, taskHandler(), 0, 20)
    private var bossBar: BossBar? = null
    private val vaccine = selectVaccine()
    private var enableProtector = false

    init {
        Bukkit.getPluginManager().registerEvents(this, Mho.instance)
        makeBossBar(true)
        Utils.sendBroadcast("現在、世界中で新型ウイルスが蔓延しております。")
        Utils.sendBroadcast("感染対策として、ソーシャルディスタンスを保ってください。")
        Utils.sendBroadcast("研究によると「${vaccine.name}」を摂取することで抗体ができることが確認されております。")
    }

    private fun selectVaccine(): Material =
        Material.values().filter { it.isEdible }.random()

    private fun makeBossBar(enable: Boolean) {
        if (!enable) {
            bossBar?.removeAll()
            bossBar?.progress = 0.0
            bossBar = null
            return
        }
        val title = "${ChatColor.BOLD}【ワクチン】 ${vaccine.name} 【残り時間】${Utils.timeToText(currentTime)}"
        val bossBar = bossBar ?: Bukkit.getServer().createBossBar(
            NamespacedKey(Mho.instance, "mho"),
            title,
            BarColor.RED,
            BarStyle.SOLID
        )
        bossBar.setTitle(title)
        players.forEach { bossBar.addPlayer(it) }
        bossBar.progress = currentTime.toDouble() / LIMIT.toDouble()
        this.bossBar = bossBar
    }

    private fun taskHandler(): Runnable = Runnable {
        if (currentTime < 1) {
            players.forEach { it.sendTitle("終焉", "THE ENDってね", 10, 70, 10) }
            destroy()
            return@Runnable
        }
        currentTime--
        makeBossBar(true)
    }

    fun destroy() {
        currentTask.cancel()
        HandlerList.unregisterAll(this)
        makeBossBar(false)
        State.core = null
    }

    fun getAward() {
        if (!enableProtector) return
        players.forEach { it.sendTitle("終焉", "パンデミックが収束しました！！", 10, 70, 10) }
        players.forEach { it.sendMessage("【今年の運勢】 ${setOf("大吉", "中吉", "小吉", "凶", "大凶").random()}") }
        destroy()
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (enableProtector) return
        val targets = event.player.world.getNearbyEntities(event.player.location, 3.0, 3.0, 3.0)
        if (targets.filterIsInstance<Mob>().isEmpty()) return
        event.player.health = if (event.player.health < 0.5) 0.0 else event.player.health - 0.5
        if (event.player.health < 0.1) Utils.sendBroadcast("${event.player.name}がウイルスに感染しました")
    }

    @EventHandler
    fun onConsume(event: PlayerItemConsumeEvent) {
        val item = ItemStack(vaccine)
        enableProtector = item.isSimilar(event.item)
        if (enableProtector) {
            Utils.sendBroadcast("抗体が確認できました。")
        }
    }
}