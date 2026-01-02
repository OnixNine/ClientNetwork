package me.onixdev.ircchat.security

import java.nio.charset.Charset
import java.util.*


object Encrypting {
    fun encrypt(str:String):String{
        return Base64.getEncoder().encodeToString(str.toByteArray())
    }
    fun decrypt(str:String):String{
        return Base64.getDecoder().decode(str.toByteArray()).toString(Charset.defaultCharset())
    }
}