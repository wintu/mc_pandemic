package dev.wintu.mho

import org.bukkit.Bukkit

class Utils {
    companion object {
        fun sendBroadcast(message: String) {
            Bukkit.broadcastMessage("[MHO]: $message")
        }

        fun timeToText(time: Int): String {
            return if (time == 0) {
                "00:00"
            } else {
                val m = time % 3600 / 60
                val s = time % 60
                "%1$02d:%2$02d".format(m, s)
            }
        }
    }
}