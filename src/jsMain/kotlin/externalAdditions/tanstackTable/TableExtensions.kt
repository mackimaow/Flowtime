package externalAdditions.tanstackTable

import data.Distraction
import kotlinx.js.ReadonlyArray
import kotlinx.js.jso
import react.ReactNode
import tanstack.table.core.*
import kotlin.reflect.KProperty1

@DslMarker
annotation class ColumnBaseBuilderMarker


sealed class ColumnBaseBuilder<TData: RowData, TValue: Any> {
    var id: String? = null

    var header: Any? = null
    private set

    fun header(value: String) { header = value }

    fun header(value: (HeaderContext<TData, TValue>) -> ReactNode) { header = value }

    var cell: Any? = null
        private set

    fun cell(value: String) { cell = value }

    fun cell(value: (CellContext<TData, TValue>) -> ReactNode) { cell = value }

    var footer: Any? = null
        private set

    fun footer(value: String) { footer = value }

    fun footer(value: (HeaderContext<TData, TValue>) -> ReactNode) { footer = value }

    val meta: ColumnMeta? = null
}

sealed external interface ColumnContent {
    var id: dynamic
    var header: dynamic
    var cell: dynamic
    var footer: dynamic
    var meta: dynamic
}


@ColumnBaseBuilderMarker
class ColumnContentBuilder <TData: RowData, TValue: Any> internal constructor(
): ColumnBaseBuilder<TData, TValue>() {

    internal fun getValue(): Any {
        val builder = this
        return jso<ColumnContent> {
            builder.id?.also { id = it }
            builder.header?.also { header = it }
            builder.cell?.also { cell = it }
            builder.footer?.also { footer = it }
            builder.meta?.also { meta = it }
        }
    }
}


sealed external interface GroupContent: ColumnContent {
    var columns: dynamic
}


@ColumnBaseBuilderMarker
class ColumnGroupBuilder <TData: RowData, TValue: Any> internal constructor(
    private val columnHelper: ColumnHelper<TData> = createColumnHelper()
): ColumnBaseBuilder<TData, TValue>() {

    var columns: ReadonlyArray<ColumnDef<TData, *>>? = null

    fun columns (build: ColumnBuilder<TData>.() -> Unit) {
        val builder = ColumnBuilder(columnHelper)
        builder.build()
        columns = builder.getValue()
    }

    internal fun getValue(): Any {
        val builder = this
        return jso<GroupContent> {
            builder.id?.also { id = it }
            builder.header?.also { header = it }
            builder.cell?.also { cell = it }
            builder.footer?.also { footer = it }
            builder.meta?.also { meta = it }
            builder.columns?.also { columns = it }
        }
    }
}


fun interface CustomAccessor<TData: RowData, TValue: Any> {
    fun column (column: ColumnContentBuilder<TData, TValue>.() -> Unit)
}

@ColumnBaseBuilderMarker
class ColumnBuilder<TData: RowData> internal constructor(
    private val columnHelper: ColumnHelper<TData> = createColumnHelper()
) {
    private val items: MutableList<ColumnDef<TData, *>> = mutableListOf()

// --- The following doesn't work on the IR compiler for JS, but it does work for LEGACY ---
//    fun <TValue: Any> accessor(
//        accessor: KProperty1<TData, TValue>,
//        column: ColumnContentBuilder<TData, TValue>.() -> Unit
//    ) {
//        val viewBuilder = ColumnContentBuilder<TData, TValue>()
//        viewBuilder.column()
//        val columnAccessor = columnHelper.accessor<TValue>(
//            accessor.name,
//            viewBuilder.getValue()
//        )
//        items.add(columnAccessor)
//    }

    fun <TValue: Any> accessor(
        accessor: (TData) -> TValue
    ): CustomAccessor<TData, TValue> {
        return CustomAccessor {
            val viewBuilder = ColumnContentBuilder<TData, TValue>()
            viewBuilder.it()
            val columnAccessor = columnHelper.accessor<TValue>(
                accessor,
                viewBuilder.getValue()
            )
            items.add(columnAccessor)
        }
    }

    fun display(
        column: ColumnContentBuilder<TData, *>.() -> Unit
    ) {
        val viewBuilder = ColumnContentBuilder<TData, Any>()
        viewBuilder.column()
        val columnDisplay = columnHelper.display(
            viewBuilder.getValue().unsafeCast<DisplayColumnDef<TData>>()
        )
        items.add(columnDisplay)
    }

    fun group(
        column: ColumnGroupBuilder<TData, *>.() -> Unit
    ) {
        val viewBuilder = ColumnGroupBuilder<TData, Any>()
        viewBuilder.column()
        val columnDisplay = columnHelper.group(
            viewBuilder.getValue().unsafeCast<GroupColumnDef<TData>>()
        )
        items.add(columnDisplay)
    }

    internal fun getValue(): ReadonlyArray<ColumnDef<TData, *>> {
        return items.toTypedArray()
    }
}

fun <TData: RowData> buildColumns(
    build: ColumnBuilder<TData>.() -> Unit
): ReadonlyArray<ColumnDef<TData, *>> {
    val builder = ColumnBuilder<TData>()
    builder.build()
    return builder.getValue()
}

fun <TData : RowData> buildTableOptions(
    builder: TableOptionsResolved<TData>.() -> Unit
): TableOptions<TData> {
    return jso(builder)
}