package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command

class UpdateRoleCommand : Command("role","dd","s") {
    override fun execute(args: Array<String?>) {
        if (args.size>1) {
            val userName = args[0]
            val role = args[1]
            Server.INSTANCE.getDataBase().updateRole(userName.toString(), role.toString())
            print("Role $userName Updated to $role")
        }
    }
}