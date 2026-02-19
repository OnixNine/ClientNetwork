package me.onixdev.ircchat.service.database

import com.github.groundbreakingmc.mylib.database.Database
import com.github.groundbreakingmc.mylib.database.InsertQuery
import com.github.groundbreakingmc.mylib.database.SelectQuery
import com.github.groundbreakingmc.mylib.database.UpdateQuery
import io.github.oshai.kotlinlogging.KotlinLogging
import java.sql.SQLException
import kotlin.system.exitProcess


class DataBaseService {
    lateinit var db: Database
    private var findById: SelectQuery? = null
    lateinit var findByUsername: SelectQuery
    lateinit var createUser: InsertQuery
    lateinit var updateRole: UpdateQuery
    val logger = KotlinLogging.logger("DataBaseLogger")
    init {
        init()
    }

    private fun init() {

        try {
            Class.forName("org.sqlite.JDBC")
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
        db = Database.sqlite("app.db")
        try {
            db.connection()
            db.createTables("CREATE TABLE IF NOT EXISTS users(" + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "username VARCHAR(255) NOT NULL, " + "password VARCHAR(255) NOT NULL, " + "role VARCHAR(255) NOT NULL, " + "joined INTEGER NOT NULL)")
            this.findByUsername = db.select()
                .from("users")
                .where("username = ?")
                .prepare()

            this.createUser = db.insert("users")
                .value("username", "")
                .value("password", "")
                .value("role", "user")
                .value("joined", 0)
                .prepare()
            this.updateRole = db.update("users")
                .set("role", "?")
                .where("username = ?")
                .prepare()
            this.findById = db.select()
                .from("users")
                .where("id = ?")
                .prepare()
        } catch (e: SQLException) {
            logger.error { "Error While Init DataBase E: ${e.message}}"  }
            exitProcess(100)
        }
    }

    fun findByUserName(username: String): TempedIrcEntity {
        try {
            return db.select()
                .from("users")
                .where("username = ?", username)
                .fetchFirst {
                    TempedIrcEntity(
                        it.getString("username"),
                        it.getString("password"),
                        it.getString("role"),
                        it.getInt("joined") == 1
                    )
                }
        } catch (e: Exception) {
            println(e.stackTraceToString())
            return TempedIrcEntity("invalid","aut","user",false)
        }
    }

    @Throws(SQLException::class)
    fun create(username: String?, email: String?) {
        createUser.execute(username!!, email!!, "user", 1)
    }

    @Throws(SQLException::class)
    fun findById(id: Int): TempedIrcEntity {
        return db.select().from("users").where("id = ?",id).fetchFirst {
            TempedIrcEntity(
                it.getString("username"),
                it.getString("password"),
                it.getString("role"),
                it.getInt("joined") == 1
            )
        }
    }
    fun updateRole(username: String, role: String) {
        db.update("users")
            .set("role", role)
            .where("username = ?", username)
            .execute()
    }
}