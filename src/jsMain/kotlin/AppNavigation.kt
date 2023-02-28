import csstype.*
import data.UserSession
import data.shutdown
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mui.icons.material.Menu
import mui.icons.material.PowerSettingsNew
import mui.material.*
import mui.material.Size
import mui.material.Tab
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.w3c.dom.HTMLButtonElement
import react.*
import react.dom.aria.ariaLabel
import react.dom.events.MouseEvent
import react.dom.events.SyntheticEvent
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.nav


data class TabChild (
    var label: ReactNode? = null,
    var icon: ReactNode? = null,
    var tabContent: ReactNode? = null
) {
    companion object {
        fun build (builder: (TabChild.() -> Unit)?): TabChild {
            val tabChild = TabChild()
            if (builder != null)
                tabChild.builder()
            return tabChild
        }
    }
}


external interface AppNavigationProps: Props {
    var openTabs: Boolean
    var tabValue: String
    var onTabChange:  ((SyntheticEvent<*, *>, dynamic) -> Unit)?
    var onMenuToggle: (MouseEvent<HTMLButtonElement, *>, Boolean) -> Unit
    var tabs: MutableMap<String, TabChild>
}


val AppNavigation = FC<AppNavigationProps> {props ->
    var shutdownDialogOpen by useState(false)
    PromptContinueDialog {
        open = shutdownDialogOpen
        title = "Shutdown FlowTime?"
        contentMessage = (
            "Are you sure you want to shutdown FlowTime?"
            + " Your flow-sessions will be saved and you will"
            + " immediately be transferred into break session on shutdown."
        )
        onClose = {
            shutdownDialogOpen = false
        }
        onContinue = {
            scope.launch {
                UserSession.shutdown()
                shutdownDialogOpen = false
                delay(5000)
                window.location.replace("http://www.google.com")
            }
        }
        actionText = "Shutdown"
        actionButtonColor = ButtonColor.error
    }
    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            height = 100.vh
            width = 100.vw
        }
        AppBar {
            component = nav
            position = AppBarPosition.static
            Toolbar {
                sx {
                    display = Display.flex
                }
                IconButton {
                    size = Size.large
                    edge = IconButtonEdge.start
                    color = IconButtonColor.inherit
                    ariaLabel = "menu"
                    sx {
                        marginRight = 1.px
                    }
                    onClick = {
                        props.onMenuToggle(it, false)
                    }
                    Menu()
                }
                Typography {
                    variant = TypographyVariant.h6
                    component = ReactHTML.div
                    sx {
                        flexGrow = number(1.0)
                    }
                    +"Flowtime"
                }

                IconButton {
                    PowerSettingsNew {}
                    onClick = {
                        shutdownDialogOpen = true
                    }
                }
            }
        }
        Box {
            sx {
                flexGrow = number(1.0)
                display = Display.flex
                minHeight = 0.px
            }
            for ((tabKey, tabChild) in props.tabs) {
                Box {
                    hidden = (props.tabValue != tabKey)
                    key = tabKey
                    sx {
                        flexGrow = number(1.0)
                        overflowY = Auto.auto
                    }
                    children = tabChild.tabContent
                }
            }
        }
    }
    Box {
        component = nav
        Drawer {
            anchor = DrawerAnchor.left
            open = props.openTabs
            onClose = { e, _ ->
                props.onMenuToggle(e, true)
            }

            Tabs {
                value = props.tabValue
                orientation = Orientation.vertical
                onChange = props.onTabChange
                for ((name, tabChild) in props.tabs) {
                    Tab {
                        value = name
                        key = name
                        label = tabChild.label
                        icon = tabChild.icon
                        iconPosition = IconPosition.start
                    }
                }
            }
        }
    }
}