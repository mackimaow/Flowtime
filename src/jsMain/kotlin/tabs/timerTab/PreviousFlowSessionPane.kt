package tabs.timerTab

import contextModules.DataContext
import contextModules.DistractionsContext
import contextModules.InitializedContext
import data.FlowSession
import data.getLatest
import externalAdditions.reactTransitionGroup.TransitionGroup
import externalAdditions.mui.contentCentered
import kotlinx.coroutines.launch
import mui.material.*
import react.*
import scope

val PreviousFlowSessionPane = FC<Props> {
    val userData by useContext(DataContext)
    val initialized = useContext(InitializedContext)
    val distractions = useContext(DistractionsContext)
    var latestFlowSessions by useState<List<FlowSession>>(listOf())

    fun updateSessions() {
        scope.launch {
            latestFlowSessions = FlowSession.getLatest(2)
        }
    }

    useEffect(userData, distractions) {
        if (initialized) {
            updateSessions()
        }
    }

    if (latestFlowSessions.isEmpty()) {
        Box {
            contentCentered = true
            Typography {
                + "No work or break sessions have been done yet."
            }
        }
    } else {
        Stack {
            spacing = 2.asDynamic()
            TransitionGroup {
                for (flowSession in latestFlowSessions) {
                    Collapse {
                        key = "${flowSession.id}"
                        FlowSessionPane {
                            this.flowSession = flowSession
                            onChange = { _, _ ->
                                updateSessions()
                            }
                        }
                    }
                }
            }
        }
    }
}
