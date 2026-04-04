package me.onixdev.ircchat.service.database

data class TempedIrcEntity(val userName: String, val passWord: String, val role: String, val beforeJoined: Boolean,val banned: Boolean)