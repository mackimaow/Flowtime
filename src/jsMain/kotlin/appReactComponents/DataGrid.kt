package appReactComponents

import contextModules.ThemeContext
import csstype.PropertiesBuilder
import csstype.px
import externalAdditions.tanstackTable.buildTableOptions
import kotlinx.js.ReadonlyArray
import kotlinx.js.jso
import mui.icons.material.*
import mui.material.*
import org.w3c.dom.HTMLButtonElement
import react.*
import react.dom.aria.ariaLabel
import react.dom.events.MouseEvent
import react.dom.html.ReactHTML.div
import tanstack.react.table.flexRender
import tanstack.react.table.useReactTable
import kotlin.math.ceil
import kotlin.math.max
import externalAdditions.tanstackTable.react.*
import mui.material.Box
import mui.system.*
import reactUtils.ReactBox
import tanstack.table.core.*
import tanstack.table.core.SortDirection

external interface DataGridProps: Props {
    var hasFooter: Boolean?
    var hasHeader: Boolean?
    var hasBody: Boolean?
    var columns: ReadonlyArray<ColumnDef<*, *>>?
    var data: ReactBox<List<*>>?
    var tableSx: SxProps<Theme>?
    var tableContainerSx: SxProps<Theme>?
}

inline fun DataGridProps.tableSx(
    crossinline block: PropertiesBuilder.() -> Unit
) {
    tableSx = jso(block)
}

inline fun DataGridProps.tableContainerSx(
    crossinline block: PropertiesBuilder.() -> Unit
) {
    tableContainerSx = jso(block)
}


sealed external interface PageOptions {
    var label: String
    var value: Int
}


val DataGrid = FC<DataGridProps> { props ->
    val (sorting, setSorting) = useState<SortingState>(arrayOf())
    val data = useMemo(props.data) {
        (props.data?.value?.toTypedArray() as ReadonlyArray<RowData>?)!!
    }
    val table = useReactTable(
        buildTableOptions {
            columns = (props.columns as ReadonlyArray<ColumnDef<RowData, *>>?)!!
            this.data = data
            state = jso {
                this.sorting = sorting
            }
            onSortingChange = setSorting.asDynamic()
            getCoreRowModel = getCoreRowModel()
            getFilteredRowModel = getFilteredRowModel()
            getPaginationRowModel = getPaginationRowModel()
            getSortedRowModel = getSortedRowModel()
        }
    )

    TableContainer {
        if (props.tableContainerSx != null) {
            sx = props.tableContainerSx
        } else {
            sx {
                margin = 5.px
                padding = 10.px
            }
        }
        Table {
            if (props.tableSx != null) {
                sx = props.tableSx
            }
            if (props.hasHeader == true)
                TableHead {
                    table.getHeaderGroups().forEach { headerGroup ->
                        TableRow {
                            key = headerGroup.id
                            headerGroup.headers.forEach { header ->
                                TableCell {
                                    key = header.id
                                    colSpan = header.colSpan
                                    if (!header.isPlaceholder && header.column.columnDef.header != null)
                                        + flexRender(
                                            header.column.columnDef.header.unsafeCast<ReactNode>(),
                                            header.getContext().unsafeCast<Props>()
                                        )
                                }
                            }
                        }
                    }
                }
            if (props.hasBody == true)
                TableBody {
                    table.getRowModel().rows.forEach { row ->
                        TableRow {
                            key = row.id
                            row.getVisibleCells().forEach { cell ->
                                TableCell {
                                    key = cell.id
                                    +flexRender(
                                        cell.column.columnDef.cell.unsafeCast<ReactNode>(),
                                        cell.getContext().unsafeCast<Props>()
                                    )
                                }
                            }
                        }
                    }
                }
            if (props.hasFooter == true)
                TableFooter {
                    table.getFooterGroups().forEach { footerGroup ->
                        TableRow {
                            key = footerGroup.id
                            footerGroup.headers.forEach { header ->
                                TableCell {
                                    key = header.id
                                    colSpan = header.colSpan
                                    if (!header.isPlaceholder && header.column.columnDef.footer != null)
                                        +flexRender(
                                            header.column.columnDef.footer.unsafeCast<ReactNode>(),
                                            header.getContext().unsafeCast<Props>()
                                        )
                                }
                            }
                        }
                    }
                }
        }

        val pagination = table.getState().pagination
        TablePagination {
            rowsPerPageOptions = arrayOf(
                5, 10, 25,
                jso<PageOptions> {
                    label = "All"
                    value = props.data?.value?.size ?: 0
                }
            )
            count = table.getFilteredRowModel().rows.size
            page = pagination.pageIndex
            rowsPerPage = pagination.pageSize
            onPageChange = { _, page ->
                table.setPageIndex(page.unsafeCast<Updater<Int>>())
            }
            onRowsPerPageChange = {
                val size: dynamic = it.target.asDynamic()?.value ?: 10
                table.setPageSize(size)
            }
            component = div
            ActionsComponent = TablePaginationActions
        }
    }
}

val TablePaginationActions = FC<TablePaginationActionsProps> {
    val theme by useContext(ThemeContext)

    fun handleFirstPageButtonClick(event: MouseEvent<HTMLButtonElement, *>) {
        it.onPageChange(event, 0)
    }

    fun handleBackButtonClick(event: MouseEvent<HTMLButtonElement, *>) {
        it.onPageChange(event, it.page.toInt() - 1)
    }

    fun handleNextButtonClick (event: MouseEvent<HTMLButtonElement, *>) {
        it.onPageChange(event, it.page.toInt() + 1)
    }

    fun handleLastPageButtonClick(event: MouseEvent<HTMLButtonElement, *>){
        it.onPageChange(
            event,
            max(0.0, ceil(it.count.toInt() / it.rowsPerPage.toDouble()) - 1)
        )
    }

    Box {
        sx {
            flexShrink = 0.asDynamic()
            marginLeft = 2.5.px
        }
        IconButton {
            onClick = ::handleFirstPageButtonClick
            disabled= it.page == 0
            ariaLabel = "first page"
            if (theme.direction == Direction.rtl) {
                LastPage {}
            } else {
                FirstPage {}
            }
        }
        IconButton {
            onClick = ::handleBackButtonClick
            disabled = it.page == 0
            ariaLabel = "previous page"
            if (theme.direction == Direction.rtl)
                KeyboardArrowRight {}
            else
                KeyboardArrowLeft {}
        }
        IconButton {
            onClick = ::handleNextButtonClick
            disabled = (
                it.page.toInt()
                    >= ceil(it.count.toInt() / it.rowsPerPage.toDouble()) - 1
            )
            ariaLabel = "next page"

            if (theme.direction == Direction.rtl)
                KeyboardArrowLeft {}
            else
                KeyboardArrowRight {}
        }
        IconButton {
            onClick = ::handleLastPageButtonClick
            disabled = (
                it.page.toInt() >= ceil(it.count.toInt() / it.rowsPerPage.toDouble()) - 1
            )
            ariaLabel = "last page"
            if (theme.direction == Direction.rtl)
                FirstPage {}
            else
                LastPage {}
        }
    }
}


external interface SortButtonProps: Props {
    var column: Column<*, *>?
    var disabled: Boolean?
}

val SortButton = FC<SortButtonProps> { props ->
    IconButton {
        disabled = props.disabled
        val sortedType = props.column?.getIsSorted?.let{ it() }
        if (sortedType != null) {
            when(sortedType) {
                SortDirection.asc -> ArrowDropUp {}
                SortDirection.desc -> ArrowDropDown {}
                else -> Sort {}
            }
        } else {
            Sort {}
        }

        onClick = props.column?.getToggleSortingHandler?.let{ it() }
    }
}