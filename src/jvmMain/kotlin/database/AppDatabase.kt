package database

import database.tables.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.exposed.sql.Database as DB
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Paths
import org.jetbrains.exposed.sql.transactions.transaction as transactionOnThread

object AppDatabase {

    private var started = false
    private var database: DB? = null
    private val accessMutex = Mutex()

    suspend fun <T> transaction(transaction: Transaction.() -> T): T {
        accessMutex.withLock {
            return newSuspendedTransaction(statement = transaction)
        }
    }

    // Add tables here to be added
    private val TABLES = listOf(
        DistractionTable,
        FlowSessionTable,
        FlowSessionDistractionTable,
        UserSettingsTable,
        ServerStatusTable,
        UserDataTable,
        UserSessionTable
    )

    const val APP_DIRECTORY_NAME = ".flowtime"
    const val DATABASE_FILE_NAME = "data.db"

    val APP_DIRECTORY_PATH = run {
        val homeDirectoryPath = System.getProperty("user.home")
        arrayOf(
            homeDirectoryPath,
            APP_DIRECTORY_NAME
        ).reduce { x, y ->
            x + File.separator + y
        }
    }

    val DATABASE_PATH = run {
        arrayOf(
            APP_DIRECTORY_PATH,
            DATABASE_FILE_NAME
        ).reduce { x, y ->
            x + File.separator + y
        }
    }

    fun start() {
        if (!started) {
            Files.createDirectories(Paths.get(APP_DIRECTORY_PATH))
            database = DB.connect("jdbc:sqlite:$DATABASE_PATH", "org.sqlite.JDBC")
            started = true
        }
    }

    // creates database tables
    fun createIfNotExists() {
        if (!started) {
            throw RuntimeException("Cannot create while not started")
        }
        transactionOnThread {
            TABLES.forEach {
                SchemaUtils.create(it)
            }
        }
    }

    // deletes database tables
    fun delete() {
        if (!started) {
            throw RuntimeException("Cannot delete while not started")
        }
        transactionOnThread {
            TABLES.forEach {
                SchemaUtils.drop(it)
            }
        }
    }

    fun reset() {
        if (!started) {
            throw RuntimeException("Cannot reset while not started")
        }
        delete()
        createIfNotExists()
    }

}