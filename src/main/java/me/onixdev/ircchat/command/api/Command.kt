package me.onixdev.ircchat.command.api


abstract class Command(val name: String?, val description: String?, val usage: String?) {
    protected fun print(string: String?) {
        println(string)
    }

    abstract fun execute(args: Array<String?>)
}
