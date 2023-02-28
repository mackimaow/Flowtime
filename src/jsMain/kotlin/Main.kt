import contextModules.resolveDataChangesLoop
import data.UserData
import kotlinx.browser.document
import react.create
import react.dom.client.createRoot

fun main() {
    UserData.resolveDataChangesLoop()
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    createRoot(container).render(App.create())
}