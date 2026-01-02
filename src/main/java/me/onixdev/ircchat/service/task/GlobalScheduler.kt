package me.onixdev.ircchat.service.task

import kotlinx.coroutines.*
import kotlin.time.Duration

object GlobalScheduler {
    // идея с Bukkit <(_)>
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val tasks = mutableMapOf<String, Job>()
    fun runTaskLater(taskId: String, delay: Duration, task: suspend () -> Unit): Job {
        val job = scope.launch {
            delay(delay)
            task()
        }
        tasks[taskId] = job
        job.invokeOnCompletion {
            tasks.remove(taskId)
        }
        return job
    }

    fun runTaskTimer(taskId: String, initialDelay: Duration, period: Duration, task: suspend () -> Unit): Job {
        val job = scope.launch {
            delay(initialDelay)
            while (isActive) {
                task()
                delay(period)
            }
        }
        tasks[taskId] = job
        job.invokeOnCompletion {
            tasks.remove(taskId)
        }
        return job
    }

    fun cancelTask(taskId: String): Boolean {
        val job = tasks[taskId]
        return if (job != null) {
            job.cancel()
            tasks.remove(taskId)
            true
        } else {
            false
        }
    }
    fun cancelAll() {
        tasks.values.forEach { it.cancel() }
        tasks.clear()
    }

}