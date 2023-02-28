@file:JsNonModule
@file:JsModule("@tanstack/react-table")
package externalAdditions.tanstackTable.react

import tanstack.table.core.RowModel
import tanstack.table.core.Table

external fun getCoreRowModel(): (Table<*>) -> () -> RowModel<*>
external fun getFilteredRowModel(): (Table<*>) -> () -> RowModel<*>
external fun getPaginationRowModel(): (Table<*>) -> () -> RowModel<*>
external fun getSortedRowModel(): (Table<*>) -> () -> RowModel<*>