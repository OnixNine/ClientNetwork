package me.onixdev.ircchat.service.moderation

object PunishmentArguments {

    data class Parsed(
        val target: String,
        val duration: ParsedDuration,
        val reason: String
    )

    fun parse(args: List<String>, defaultReason: String): Parsed? {
        if (args.isEmpty()) return null

        val target = args[0]
        var index = 1
        var duration: ParsedDuration = ParsedDuration.Permanent

        if (args.size > 1) {
            when (val parsed = DurationParser.parse(args[1])) {
                is ParsedDuration.Temporary -> {
                    duration = parsed
                    index = 2
                }

                ParsedDuration.Permanent -> {
                    duration = parsed
                    index = 2
                }

                ParsedDuration.NotADuration -> Unit
            }
        }

        val reason = args.drop(index).joinToString(" ").trim().ifBlank { defaultReason }
        return Parsed(target, duration, reason)
    }
}
