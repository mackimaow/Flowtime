import appReactComponents.AppBackdrop
import mui.material.*
import react.FC
import react.Props
import react.useState

typealias AcknowledgeComplete = () -> Unit

external interface PromptContinueDialogProps: Props {
    var open: Boolean
    var title: String
    var contentMessage: String
    var onClose: () -> Unit
    var onContinue: (AcknowledgeComplete) -> Unit
    var actionText: String?
    var actionButtonColor: ButtonColor?
}

val PromptContinueDialog = FC<PromptContinueDialogProps> { props ->
    var backdropOpen by useState(false)

    AppBackdrop {
        open = backdropOpen
    }
    Dialog {
        open = props.open
        onClose = { _, _ ->
            props.onClose()
        }
        DialogTitle {
            + props.title
        }
        DialogContent {
            DialogContentText {
                + props.contentMessage
            }
        }
        DialogActions {
            Button {
                variant = ButtonVariant.outlined
                color = ButtonColor.error
                + "Cancel"
                onClick = {
                    props.onClose()
                }
            }
            Button {
                variant = ButtonVariant.contained
                color = props.actionButtonColor ?: ButtonColor.success
                + (props.actionText ?: "Continue")
                onClick = {
                    backdropOpen = true
                    props.onContinue {
                        backdropOpen = false
                        props.onClose()
                    }
                }
            }
        }
    }
}