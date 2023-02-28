package reactUtils

import kotlin.reflect.KProperty

interface Delegator<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}