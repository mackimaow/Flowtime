package tabs.distractionsTab


import PromptContinueDialog
import appReactComponents.DataGrid
import appReactComponents.SortButton
import contextModules.DistractionsContext
import csstype.px
import data.Distraction
import data.delete
import externalAdditions.mui.contentCentered
import externalAdditions.tanstackTable.buildColumns
import kotlinx.coroutines.launch
import kotlinx.datetime.toJSDate
import kotlinx.js.jso
import mui.icons.material.Add
import mui.icons.material.Delete
import mui.icons.material.Edit
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.*
import reactUtils.ReactBox
import scope

external interface ReadOnlyProps: InputBaseComponentProps {
    var readOnly: Boolean?
}

val DistractionsTabContent = FC<Props> {
    val allDistractions = useContext(DistractionsContext)
    var openEditDialog by useState(false)
    var openRemoveDialog by useState(false)
    var editableDistraction by useState<Distraction?>(null)

    val tableColumns = useMemo {
        buildColumns<Distraction> {
            display {
                id = "create-delete"
                header { _ ->
                    IconButton.create {
                        Add {}
                        color = IconButtonColor.success
                        onClick = {
                            openEditDialog = true
                        }
                    }
                }
                cell { cell ->
                    IconButton.create {
                        Delete {}
                        color = IconButtonColor.error
                        onClick = {
                            editableDistraction = cell.row.original
                            openRemoveDialog = true
                        }
                    }
                }
            }
            accessor {
                it.id ?: 0
            }.column {
                id = "id"
                header {
                    Typography.create {
                        + "ID"
                        SortButton {
                            column = it.column
                            disabled = false
                        }
                    }
                }
                cell {
                    Typography.create {
                        + "${it.getValue()}"
                    }
                }
            }
            accessor {
                it.tag
            }.column {
                id = "tag"
                header {
                    Typography.create {
                        variant = TypographyVariant.h6
                        + "Tag"
                        SortButton {
                            column = it.column
                            disabled = false
                        }
                    }
                }
                cell { cell ->
                    DistractionTag.create {
                        distraction = cell.row.original
                    }
                }
            }
            accessor {
                it.description
            }.column {
                id = "Description"
                header {
                    Typography.create {
                        + "Description"
                        SortButton {
                            column = it.column
                            disabled = false
                        }
                    }
                }
                cell {
                    TextField.create {
                        id = "outlined-multiline-flexible"
                        value = it.getValue()
                        multiline = true
                        inputProps = jso<ReadOnlyProps> {
                            readOnly = true
                        }
                        maxRows = 4
                    }
                }
            }
            accessor {
                it.created.toJSDate()
            }.column {
                id = "created"
                header {
                    Typography.create {
                        + "Created"
                        SortButton {
                            column = it.column
                            disabled = false
                        }
                    }
                }
                cell {
                    Typography.create {
                        + it.getValue().toLocaleString()
                    }
                }
            }
            display {
                id = "edit"
                cell { cell ->
                    Typography.create {
                        IconButton {
                            Edit {}
                            onClick = {
                                openEditDialog = true
                                editableDistraction = cell.row.original
                            }
                        }
                    }
                }
            }
        }
    }


    PromptContinueDialog {
        open = openRemoveDialog
        title = "Delete Distraction"
        contentMessage = "Are you sure you want to delete this distraction?"
        onClose = {
            openRemoveDialog = false
        }
        onContinue = { complete ->
            scope.launch {
                Distraction.delete(editableDistraction!!)
                allDistractions.update {
                    remove(editableDistraction)
                    this
                }
                editableDistraction = null
                complete()
            }
        }
        actionText = "Delete"
        actionButtonColor = ButtonColor.error
    }

    EditDistractionDialog {
        distraction = editableDistraction
        open = openEditDialog
        onClose = { newDistraction ->
            if (newDistraction != null) {
                // if editing an already created distraction
                if (editableDistraction != null) {
                    allDistractions.update {
                        val index = indexOfFirst { distraction ->
                            distraction.id == newDistraction.id
                        }
                        this[index] = newDistraction
                        this
                    }
                } else {
                    // if editing a new distraction
                    allDistractions.update {
                        add(newDistraction)
                        this
                    }
                }
            }
            openEditDialog = false
            editableDistraction = null
        }
    }

    Box {
        contentCentered = true
        Paper {
            sx {
                margin = 10.px
                padding = 20.px
            }
            Typography {
                sx {
                    padding = 20.px
                }
                variant = TypographyVariant.h3
                + "Distractions"
            }
            Divider {
                sx {
                    marginBottom = 40.px
                }
            }
            DataGrid {
                hasHeader = true
                hasBody = true
                columns = tableColumns
                data = allDistractions.unsafeCast<ReactBox<List<*>>>()
            }
        }
    }
}