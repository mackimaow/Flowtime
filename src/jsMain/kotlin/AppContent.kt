import contextModules.InitializedContext
import appReactComponents.AppBackdrop
import mui.icons.material.*
import mui.material.Typography
import react.*
import externalAdditions.reactCookie.get
import externalAdditions.reactCookie.useCookies
import tabs.distractionsTab.DistractionsTabContent
import tabs.statsTab.StatsTabContent
import tabs.settingsTab.SettingsTab
import tabs.timerTab.TimerTab


val AppContent = FC<Props> { _ ->
    val cookies = useCookies(listOf("tabOpened"))
    var tabOpened by cookies.get<String>("tabOpened")
    val isInitialized = useContext(InitializedContext)
    var stateOpenTabs by useState(false)
    var stateTabValue by useState(tabOpened ?: "Timer")

    AppBackdrop {
        open = !isInitialized
    }

    AppNavigation {
        openTabs = stateOpenTabs
        tabValue = stateTabValue
        onTabChange = { _: Any, newTabValue: String ->
            stateTabValue = newTabValue
            tabOpened = newTabValue
            stateOpenTabs = false
        }
        onMenuToggle = { _, isOnClose ->
            stateOpenTabs = if (isOnClose) false else !stateOpenTabs
        }
        tabs = mutableMapOf(
            "Timer" to TabChild.build {
                label = Typography.create() {
                    +"Timer"
                }
                icon = Timer.create()
                tabContent = TimerTab.create()
            },
            "Distractions" to TabChild.build {
                label = Typography.create() {
                    +"Distractions"
                }
                icon = Notifications.create()
                tabContent = DistractionsTabContent.create()
            },
            "Stats" to TabChild.build {
                label = Typography.create() {
                    +"Stats"
                }
                icon = QueryStats.create()
                tabContent = StatsTabContent.create() {
                    +"Stats"
                }
            },
            "Settings" to TabChild.build {
                label = Typography.create() {
                    +"Settings"
                }
                icon = Settings.create()
                tabContent = SettingsTab.create()
            }
        )
    }
}