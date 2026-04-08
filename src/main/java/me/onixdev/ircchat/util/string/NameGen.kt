package me.onixdev.ircchat.util.string

import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.max

@SuppressWarnings("all")
@Suppress("all")
object NameGen {
    private const val CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SPECIAL = "_$"

    private val ALL_CHARS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL
    private val FIRST_CHAR = LOWERCASE + UPPERCASE + SPECIAL


    fun getRandomString(length: Int): String {
        val sb = StringBuilder(length)
        val random = ThreadLocalRandom.current()

        sb.append(FIRST_CHAR[random.nextInt(FIRST_CHAR.length)])

        for (i in 1..<length) {
            sb.append(ALL_CHARS[random.nextInt(ALL_CHARS.length)])
        }

        return sb.toString()
    }


    fun getRandomString(minLength: Int, maxLength: Int): String {
        var minLength = minLength
        var maxLength = maxLength
        if (minLength < 1) minLength = 1
        if (maxLength < minLength) maxLength = minLength

        val length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1)
        return getRandomString(length)
    }


    fun getRandomLowercase(length: Int): String {
        val sb = StringBuilder(length)
        val random = ThreadLocalRandom.current()

        for (i in 0..<length) sb.append(LOWERCASE[random.nextInt(LOWERCASE.length)])

        return sb.toString()
    }


    fun getRandomUppercase(length: Int): String {
        val sb = StringBuilder(length)
        val random = ThreadLocalRandom.current()

        for (i in 0..<length) sb.append(UPPERCASE[random.nextInt(UPPERCASE.length)])

        return sb.toString()
    }


    fun getRandomDigits(length: Int): String {
        val sb = StringBuilder(length)
        val random = ThreadLocalRandom.current()

        for (i in 0..<length) {
            sb.append(DIGITS.get(random.nextInt(DIGITS.length)))
        }

        return sb.toString()
    }


    fun getConfusingString(length: Int): String {
        val confusing = "lI1oO0"
        val sb = StringBuilder(length)
        val random = ThreadLocalRandom.current()


        sb.append("lIoO".get(random.nextInt(4)))

        for (i in 1..<length) {
            sb.append(confusing.get(random.nextInt(confusing.length)))
        }

        return sb.toString()
    }

    fun getRandomCamelCase(minLength: Int, maxLength: Int): String {
        val length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1)
        val sb = StringBuilder(length)
        val random = ThreadLocalRandom.current()

        var uppercase = true
        for (i in 0..<length) {
            if (uppercase) {
                sb.append(UPPERCASE.get(random.nextInt(UPPERCASE.length)))
                uppercase = false
            } else {
                sb.append(LOWERCASE.get(random.nextInt(LOWERCASE.length)))
                if (random.nextInt(5) == 0) {
                    uppercase = true
                }
            }
        }

        return sb.toString()
    }


    fun getRandomUnicode(index: Int): String {
        val length = max(5, 25 + (index % 30))
        val sb = StringBuilder(length)

        val random = ThreadLocalRandom.current()


        for (i in 0..<length) {
            val charType = (index + i) % 5

            val c: Char = when (charType) {
                0 -> CHAR_POOL[(index + i * 17) % CHAR_POOL.length]
                1 -> (0x200B + ((index + i) % 5)).toChar() // \u200B-\u200F
                2 -> (0x0300 + ((index + i * 7) % 50)).toChar() // \u0300-\u0331
                3 -> (0x4E00 + ((index + i * 13) % 100)).toChar() // CJK
                else -> (0x0600 + ((index + i * 11) % 50)).toChar() // Arabic
            }

            sb.append(c)
        }


        if (sb.length == 0) {
            sb.append((0x200B + (index % 5)).toChar())
        }

        return sb.toString()
    }

    fun getRandomUnicode(minLen: Int, maxLen: Int): String {
        val random = ThreadLocalRandom.current()


        val length = if (minLen >= maxLen) minLen else random.nextInt(minLen, maxLen + 1)

        val sb = StringBuilder(length)

        for (i in 0..<length) {
            val charType = random.nextInt(5)

            val c: Char = when (charType) {
                0 -> CHAR_POOL[random.nextInt(CHAR_POOL.length)]
                1 -> (0x200B + random.nextInt(5)).toChar()
                2 -> (0x0300 + random.nextInt(50)).toChar()
                3 -> (0x4E00 + random.nextInt(100)).toChar()
                else -> (0x0600 + random.nextInt(50)).toChar()
            }
            sb.append(c)
        }


        if (sb.length == 0) {
            sb.append((0x200B + random.nextInt(5)).toChar())
        }

        return sb.toString()
    }

    fun getRandomUnicode(index: Int, minLen: Int, maxLen: Int): String {
        val range = max(1, maxLen - minLen + 1)
        val length = minLen + abs(index % range)

        val sb = StringBuilder(length)
        val seed = index

        for (i in 0..<length) {
            val charType = Math.floorMod(seed + i, 5)
            val c: Char

            when (charType) {
                0 -> {
                    val poolIdx = Math.floorMod(seed + i * 17, CHAR_POOL.length)
                    c = CHAR_POOL.get(poolIdx)
                }

                1 -> c = (0x200B + Math.floorMod(seed + i, 5)).toChar()
                2 -> c = (0x0300 + Math.floorMod(seed + i * 7, 50)).toChar()
                3 -> c = (0x4E00 + Math.floorMod(seed + i * 13, 100)).toChar()
                else -> c = (0x0600 + Math.floorMod(seed + i * 11, 50)).toChar()
            }
            sb.append(c)
        }


        if (sb.length == 0) {
            sb.append((0x200B + Math.floorMod(index, 5)).toChar())
        }

        return sb.toString()
    }

    fun getRandomUnicode1(len: Int): String {
        var len = len
        if (len <= 0) len = 1
        val sb = StringBuilder(len)

        val seed = ThreadLocalRandom.current().nextInt()

        for (i in 0..<len) {
            val charType = Math.floorMod(seed + i, 5)
            val c: Char

            when (charType) {
                0 -> c = CHAR_POOL.get(Math.floorMod(seed + i * 17, CHAR_POOL.length))
                1 -> c = (0x200B + Math.floorMod(seed + i, 5)).toChar()
                2 -> c = (0x0300 + Math.floorMod(seed + i * 7, 50)).toChar()
                3 -> c = (0x4E00 + Math.floorMod(seed + i * 13, 100)).toChar()
                else -> c = (0x0600 + Math.floorMod(seed + i * 11, 50)).toChar()
            }
            sb.append(c)
        }
        return sb.toString()
    }


    fun getRandomInvisible(length: Int): String {
        val invisible = charArrayOf(
            '\u200B',
            '\u200C',
            '\u200D',
            '\u2060',
            '\uFEFF'
        )

        val sb = StringBuilder(length)
        val random = ThreadLocalRandom.current()

        for (i in 0..<length) {
            sb.append(invisible[random.nextInt(invisible.size)])
        }

        return sb.toString()
    }


    fun getRandomStringWithPrefix(prefix: String?, length: Int): String {
        return prefix + getRandomString(length)
    }


    fun getRandomStringWithSuffix(length: Int, suffix: String): String {
        return getRandomString(length) + suffix
    }


    fun getRandomIdentifier(minLength: Int, maxLength: Int): String {
        val length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1)
        val letterCount = ThreadLocalRandom.current().nextInt(length / 2, length)
        val digitCount = length - letterCount

        return getRandomLowercase(letterCount) + (if (digitCount > 0) getRandomDigits(digitCount) else "")
    }


    private var counter = 0

    fun getUniqueString(prefix: String): String {
        return prefix + (counter++)
    }

    val uniqueString: String
        get() = "a" + (counter++)

    fun resetCounter() {
        counter = 0
    }
}