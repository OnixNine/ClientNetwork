package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.command.api.Command

class PrivetCommand : Command("privet", "СОсао", "") {
    override fun execute(args: Array<String?>) {
        print("aaa ${args?.size}")
        val username = args?.get(0)
        val id = args?.get(1)!!.toInt()
        print(username)
        for (s in args) println(s)
    }

}