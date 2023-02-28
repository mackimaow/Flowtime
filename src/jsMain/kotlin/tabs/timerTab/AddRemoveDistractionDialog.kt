package tabs.timerTab

import contextModules.DistractionsContext
import csstype.Display
import csstype.FlexWrap
import csstype.px
import appReactComponents.AppBackdrop
import data.Distraction
import data.FlowSession
import data.set
import externalAdditions.reactTransitionGroup.TransitionGroup
import kotlinx.coroutines.launch
import mui.material.*
import mui.system.sx
import react.*
import reactUtils.ReactBox
import scope
import tabs.distractionsTab.DistractionTag


external interface AddRemoveDistractionDialogProps: Props {
    var open: Boolean
    var onClose: (FlowSession?) -> Unit
    var flowSession: FlowSession
}

val AddRemoveDistractionDialog = FC<AddRemoveDistractionDialogProps> { props ->
    val flowSession = props.flowSession
    val sessionTypeString = if(flowSession.isBreak) "Break" else "Work"
    val currentDistractionsBox by ReactBox.useState<MutableList<Distraction>>(mutableListOf())
    val allDistractionsBox = useContext(DistractionsContext)
    var changingFlowSession by useState(false)

    val allDistractionsArray = useMemo(currentDistractionsBox) {
        currentDistractionsBox.value.toTypedArray()
    }

    useEffect(flowSession, allDistractionsBox) {
        currentDistractionsBox.update {
            // this is needed because select determines uniqueness by reference, not equality
            val values = allDistractionsBox.value.associateBy { it.id }
            flowSession.distractions.filter {
                values.containsKey(it.id)
            }.map {
                values[it.id]!!
            }.toMutableList()
        }
    }

    AppBackdrop {
        open = changingFlowSession
    }

    Dialog {
        open = props.open
        onClose = { _, _ ->
            props.onClose(null)
        }

        DialogTitle {
            + "Add or remove Distractions to $sessionTypeString Flow Session"
        }
        DialogContent {
            Stack {
                Select {
                    multiple = true
                    value = allDistractionsArray
                    onChange = { event, _ ->
                        val distractionThatChanged = event.target.value.unsafeCast<Array<Distraction>>()
                        currentDistractionsBox.update {
                            distractionThatChanged.toMutableList()
                        }
                    }
                    renderValue = { value ->
                        val distractions = value.unsafeCast<Array<Distraction>>()

                        Box.create {
                            TransitionGroup {
                                component = Box

                                unsafeCast<BoxProps>().also {
                                    it.sx {
                                        display = Display.flex
                                        flexWrap = FlexWrap.wrap
                                        gap = 0.5.px
                                    }
                                }

                                distractions.forEach { distraction ->
                                    Collapse {
                                        key = "${distraction.id}"
                                        orientation = Orientation.horizontal
                                        sx {
                                            marginTop = 5.px
                                            marginRight = 5.px
                                        }
                                        DistractionTag {
                                            this.distraction = distraction
                                        }
                                    }
                                }
                            }
                        }
                    }
                    allDistractionsBox.value.forEach { distraction ->
                        MenuItem {
                            key = "${distraction.id}"
                            value = distraction.asDynamic()
                            DistractionTag {
                                this.distraction = distraction
                            }
                        }
                    }
                }
            }
        }
        DialogActions {
            Button {
                variant = ButtonVariant.outlined
                color = ButtonColor.error
                + "Cancel"
                onClick = {
                    props.onClose(null)
                }
            }
            Button {
                variant = ButtonVariant.contained
                color = ButtonColor.success
                + "Update"
                onClick = {
                    changingFlowSession = true
                    scope.launch {
                        val newSession = flowSession.copy(
                            distractions = currentDistractionsBox.value
                        )
                        FlowSession.set(
                            newSession
                        )
                        changingFlowSession = false
                        props.onClose(newSession)
                    }
                }
            }
        }
    }
}