package dev.wintu.mho

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class MainCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name != "mho") return true
        return when(args[0]) {
            "start" -> {
                State.core = Core()
                true
            }
            "stop" -> {
                State.core?.destroy()
                true
            }
            "award" -> {
                State.core?.getAward()
                true
            }
            else -> true
        }
    }
}