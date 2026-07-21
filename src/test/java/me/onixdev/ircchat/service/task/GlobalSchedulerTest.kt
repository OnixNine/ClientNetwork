package me.onixdev.ircchat.service.task

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

class GlobalSchedulerTest {

    @AfterEach
    fun tearDown() {
        GlobalScheduler.cancelAll()
    }

    @Test
    fun `runTaskLater executes after delay`() = runBlocking {
        var executed = false
        GlobalScheduler.runTaskLater("test", 50.milliseconds) {
            executed = true
        }
        delay(100)
        assertTrue(executed)
    }

    @Test
    fun `cancelTask prevents execution`() = runBlocking {
        var executed = false
        GlobalScheduler.runTaskLater("test", 100.milliseconds) {
            executed = true
        }
        val cancelled = GlobalScheduler.cancelTask("test")
        delay(200)
        assertTrue(cancelled)
        assertFalse(executed)
    }

    @Test
    fun `cancelTask returns false for unknown id`() {
        assertFalse(GlobalScheduler.cancelTask("nonexistent"))
    }

    @Test
    fun `cancelAll stops all tasks`() = runBlocking {
        var count = 0
        GlobalScheduler.runTaskTimer("t1", 10.milliseconds, 10.milliseconds) { count++ }
        GlobalScheduler.runTaskTimer("t2", 10.milliseconds, 10.milliseconds) { count++ }
        delay(50)
        GlobalScheduler.cancelAll()
        val finalCount = count
        delay(100)
        assertEquals(finalCount, count)
    }

    @Test
    fun `task auto-removes from map after completion`() = runBlocking {
        GlobalScheduler.runTaskLater("oneshot", 10.milliseconds) { }
        delay(100)
        assertFalse(GlobalScheduler.cancelTask("oneshot"))
    }

    @Test
    fun `runTaskTimer repeats multiple times`() = runBlocking {
        var count = 0
        GlobalScheduler.runTaskTimer("repeat", 10.milliseconds, 20.milliseconds) {
            count++
        }
        delay(150)
        GlobalScheduler.cancelTask("repeat")
        assertTrue(count >= 3, "Expected at least 3 executions, got $count")
    }

    @Test
    fun `different task ids run independently`() = runBlocking {
        var a = 0
        var b = 0
        GlobalScheduler.runTaskLater("a", 10.milliseconds) { a++ }
        GlobalScheduler.runTaskLater("b", 10.milliseconds) { b++ }
        delay(100)
        assertEquals(1, a)
        assertEquals(1, b)
    }
}
