package reactUtils

import react.StateInstance
import kotlin.reflect.KProperty

internal data class ExternalResourceState<T>(
    var value: T,
    val setter: T.(T, (T) -> Unit) -> Unit,
    var state: StateInstance<ExternalResource<T>>? = null
)  {

    internal fun set(newValue: T) {
        value.setter(newValue) { modifiedNewValue ->
            state!!.component2().invoke(
                ExternalResource(this) // wrap into new external resource to force react to update
            )
            value = modifiedNewValue
        }
    }
}

class ExternalResource<T> internal constructor(
    private var resourceState: ExternalResourceState<T>
): Delegator<T> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return resourceState.value
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        resourceState.set(value)
    }

    companion object {
        // returns a cached resource (from backend) re-caches
        // value when user sets it
        fun <T> useState(obj: T, setter: T.(T, (T) -> Unit) -> Unit): ExternalResource<T> {
            val state = react.useState(
                ExternalResource(
                    ExternalResourceState(obj, setter)
                )
            )
            state.component1().resourceState.state = state
            return state.component1()
        }
    }
}