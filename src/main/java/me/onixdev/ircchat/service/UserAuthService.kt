package me.onixdev.ircchat.service

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.RIPEMD160
import dev.whyoleg.cryptography.algorithms.SHA3_512
import dev.whyoleg.cryptography.algorithms.SHA512

object UserAuthService {
    fun checkAuth(passHash:String, passWorld: String):Boolean{
        val provider = CryptographyProvider.Default.get(SHA3_512)
        val hash=   provider.hasher().hashBlocking(passHash.toByteArray())
        val hash2 = provider.hasher().hashBlocking(passWorld.toByteArray())
        return hash.contentEquals(hash2)
    }
    fun getHash(str:String):String{
        val provider = CryptographyProvider.Default.get(SHA3_512)
        val hash=   provider.hasher().hashBlocking(str.toByteArray())
        val strings = hash.contentToString().replace("[","").replace("]","")
        val str = java.lang.String(strings)
        return str.toString()
    }
}