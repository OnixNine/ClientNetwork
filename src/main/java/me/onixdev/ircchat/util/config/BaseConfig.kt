package me.onixdev.ircchat.util.config

import me.onixdev.ircchat.service.auth.Hash

data class BaseConfig(val port: Int,val timeout:Int,val hash: Hash)
