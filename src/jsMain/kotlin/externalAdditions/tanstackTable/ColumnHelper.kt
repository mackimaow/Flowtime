
// TODO: Add External Declaration Annotation to file
@file:JsNonModule
@file:JsModule("@tanstack/table-core")
package externalAdditions.tanstackTable

import tanstack.table.core.ColumnDef
import tanstack.table.core.RowData

external interface DisplayColumnDef<T: RowData>
external interface GroupColumnDef<T: RowData>

external interface ColumnHelper<TData: RowData> {
    fun <TValue: Any> accessor(
        accessor: dynamic,
        column: dynamic
    ): ColumnDef<TData, TValue>
    fun display(column: DisplayColumnDef<TData>): ColumnDef<TData, Any>
    fun group(column: GroupColumnDef<TData>): ColumnDef<TData, Any>
}

external fun <TData: RowData> createColumnHelper(): ColumnHelper<TData>
