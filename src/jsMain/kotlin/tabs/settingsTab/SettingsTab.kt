package tabs.settingsTab

import contextModules.SettingsContext
import csstype.px
import data.UserSettings
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import externalAdditions.mui.Grid2
import react.*


val SettingsTab = FC<Props> {
    var settingsState by useContext(SettingsContext)
    var workToBreakRatio by useState(UserSettings.DEFAULT_RATIO)
    var initialized by useState(false)

    useEffect(settingsState) {
        if (!initialized && settingsState != null) {
            initialized = true
            workToBreakRatio = settingsState?.workToBreakRatio ?: UserSettings.DEFAULT_RATIO
        }
    }

    Grid2 {
        container = true
        Grid2 {
            xs = 12
            md = 4
            xl = 2
            Paper {
                elevation = 1
                sx {
                    margin = 10.px
                    padding = 20.px
                }
                Stack {
                    Typography {
                        variant = TypographyVariant.h4
                        + "Settings"
                    }
                    Divider()
                    SetWorkBreakRatio {
                        actualRatio = settingsState?.workToBreakRatio ?: UserSettings.DEFAULT_RATIO
                        newRatio = workToBreakRatio
                        setRatio = {
                            workToBreakRatio = it
                        }
                    }
                    Button {
                        variant = ButtonVariant.outlined
                        color = ButtonColor.error
                        sx {
                            margin = 5.px
                        }
                        Typography {
                            + "Make Changes"
                        }
                        onClick = {
                            settingsState?.copy(
                                workToBreakRatio=workToBreakRatio
                            )?.also {
                                settingsState = it
                            }
                        }
                    }

                    Divider {
                        sx {
                            marginTop = 40.px
                        }
                    }
                    ResetUserSettings()
                }
            }
        }
        Grid2 {
            // fill automatically with space
        }
    }
}