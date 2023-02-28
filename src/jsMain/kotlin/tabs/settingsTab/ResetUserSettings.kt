package tabs.settingsTab

import PromptContinueDialog
import csstype.px
import data.UserSettings
import contextModules.delete
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.useState
import scope

var ResetUserSettings = FC<Props> {
    var dialogOpen by useState(false)

    PromptContinueDialog {
        open = dialogOpen
        title = "Reset User Data"
        contentMessage = (
            "Are you sure you want to delete all user data?"
            + " This action is non-reversible."
        )
        onClose = {
            dialogOpen = false
        }
        onContinue = {
            scope.launch {
                UserSettings.delete()
                dialogOpen = false
                delay(5000)
                window.location.reload()
            }
        }
        actionText = "Delete"
        actionButtonColor = ButtonColor.error
    }

    Box {
        sx {
            padding = 10.px
        }
        Typography {
            + "Reset User Data"
        }
        Button {
            sx {
                margin = 10.px
            }
            onClick = {
                dialogOpen = true
            }
            variant = ButtonVariant.contained
            color = ButtonColor.error
            Typography {
                + "Reset"
            }
        }
    }
}