package dev.wintu.mho

import org.bukkit.plugin.java.JavaPlugin

class Mho: JavaPlugin() {
    override fun onEnable() {
        instance = this
        getCommand("mho")?.setExecutor(MainCommand())
    }

    override fun onDisable() {
        State.core?.destroy()
    }

    companion object {
        lateinit var instance: Mho
    }

}