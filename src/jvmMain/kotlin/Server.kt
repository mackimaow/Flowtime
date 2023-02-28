import database.AppDatabase
import database.tables.*


fun main() {
    AppDatabase.start()
    AppDatabase.createIfNotExists()
    UserData.repairIfSuddenExit()
    ServerStatus.removeMainSession()
    UserSession.clearSessions()

    WebRoutes.createAndStartServer() // non-blocking
    ServerStatus.manageLoop() // blocking
}
