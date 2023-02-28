package tabs.timerTab

import appReactComponents.AppBackdrop
import data.FlowSession
import data.delete
import kotlinx.coroutines.launch
import mui.material.*
import react.FC
import react.Props
import react.useState
import scope


external interface RemoveFlowSessionDialogProps: Props {
    var flowSession: FlowSession?
    var open: Boolean
    var onClose: (FlowSession?) -> Unit
}

val RemoveFlowSessionDialog = FC<RemoveFlowSessionDialogProps> { props ->
    var deletingFlowSession by useState(false)

    AppBackdrop {
        open = deletingFlowSession
    }
    Dialog {
        open = props.open
        onClose = { _, _ ->
            props.onClose(null)
        }
        DialogTitle {
            + "Delete flow session"
        }
        DialogContent {
            + "Are you sure you want to delete this flow session?"
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
                color = ButtonColor.error
                + "Delete"
                onClick = {
                    deletingFlowSession = true
                    scope.launch {
                        FlowSession.delete(props.flowSession!!)
                        deletingFlowSession = false
                        props.onClose(props.flowSession)
                    }
                }
            }
        }
    }

}
