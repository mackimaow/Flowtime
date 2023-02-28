package tabs.distractionsTab

import csstype.TextShadow
import csstype.px
import appReactComponents.AppBackdrop
import data.Distraction
import data.set
import externalAdditions.mui.contentCentered
import externals.reactColor.CirclePicker
import externals.reactColor.Color
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.js.jso
import mui.material.*
import mui.system.responsive
import mui.system.sx
import react.*
import react.dom.onChange
import scope


external interface EditDistractionDialogProps: Props {
    var open: Boolean
    var distraction: Distraction?
    var onClose: (Distraction?) -> Unit
}

var EditDistractionDialog = FC<EditDistractionDialogProps> {props ->
    var tag by useState("")
    var description by useState("")
    var tagColor by useState("#607d8b")
    var tagIsEmpty by useState(false)
    var updatingDistraction by useState(false)

    useEffect(props.open) {
        if (props.distraction != null){
            tag = props.distraction?.tag ?: ""
            description = props.distraction?.description ?: ""
            tagColor = props.distraction?.tagColor ?: "#607d8b"
        } else {
            tag = ""
            description = ""
            tagColor = "#607d8b"
        }
        tagIsEmpty = false
    }

    AppBackdrop {
        open = updatingDistraction
    }

    Dialog {
        open = props.open
        onClose = { _, _ ->
            props.onClose(null)
        }
        DialogTitle {
            + "Edit Distraction"
        }
        DialogContent {
            Stack {
                sx {
                    padding = 10.px
                }
                spacing = responsive(2)
                TextField {
                    label = Typography.create { + "Tag*" }
                    placeholder = "Add tag here"
                    value = tag
                    error = tagIsEmpty
                    if (tagIsEmpty)
                        helperText = "Tag cannot be empty.".asDynamic()
                    onChange = { event ->
                        tag = event.target.asDynamic().value
                    }
                    inputProps = jso {
                        this.asDynamic().maxLength = 50
                    }
                }
                Collapse {
                    `in` = tag != ""
                    Stack {
                        spacing = responsive(2)
                        sx {
                            paddingBottom = 5.px
                        }
                        Box {
                            contentCentered = true
                            Chip {
                                sx {
                                    backgroundColor = tagColor.asDynamic()
                                }
                                label = Typography.create() {
                                    sx {
                                        textShadow = TextShadow(
                                            1.px,
                                            1.px,
                                            2.px,
                                            "#1e1e1e".asDynamic()
                                        )
                                    }
                                    + tag
                                }
                            }
                        }
                        Box {
                            contentCentered = true
                            CirclePicker {
                                color = Color.hex { tagColor }
                                onChange = { color, _ ->
                                    tagColor = color.hex
                                }
                            }
                        }
                    }
                }
                TextField {
                    label = Typography.create { + "Description" }
                    placeholder = "Add description here"
                    multiline = true
                    value = description
                    onChange = { event ->
                        description = event.target.asDynamic().value
                    }
                }
            }
        }
        DialogActions {
            Button {
                + "Cancel"
                variant = ButtonVariant.outlined
                color = ButtonColor.error
                onClick = {
                    props.onClose(null)
                }
            }
            Button {
                if (props.distraction == null) {
                    + "New"
                } else {
                    + "Update"
                }

                variant = ButtonVariant.contained
                color = ButtonColor.success
                onClick = {
                    if (tag != "") {
                        val newDistraction = if (props.distraction == null) {
                            // create new
                            Distraction(
                                tag,
                                tagColor,
                                description,
                                Clock.System.now(),
                            )
                        } else {
                            // otherwise create updated
                            Distraction(
                                tag,
                                tagColor,
                                description,
                                props.distraction!!.created,
                                props.distraction!!.id
                            )
                        }
                        updatingDistraction = true
                        scope.launch {
                            val newDistractionWithId = Distraction.set(newDistraction)
                            updatingDistraction = false
                            props.onClose(
                                newDistractionWithId
                            )
                        }
                    } else {
                        tagIsEmpty = true
                    }
                }
            }
        }
    }
}