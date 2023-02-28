package contextModules

import react.Context
import react.StateInstance
import react.useContext
import reactUtils.Delegator
import kotlin.reflect.KProperty

// buffers a context object;
// when a context is being 'set', subsequent 'gets' (without buffer)
// don't return the updated value on the same coroutine. With Buffers, this
// is no longer the case
sealed class BufferedContext<T>: Delegator<T> {
    private var bufferedObj: T? = null

    protected abstract fun getValue(): T
    protected abstract fun setValue(value: T)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return bufferedObj ?: getValue()
    }
    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        bufferedObj = value // set buffer
        setValue(value)
    }
    companion object{
        fun <T> use(context: Context<StateInstance<T>>): BufferedContext<T> {
            return StateInstanceBufferedContext(context)
        }
        fun <T, K: Delegator<T>> use(context: Context<K>): BufferedContext<T> {
            return DelegateBufferedContext(context)
        }
    }
}

private class StateInstanceBufferedContext<T> constructor(
    context: Context<StateInstance<T>>
): BufferedContext<T>() {
    private val contextObj = useContext(context)
    override fun getValue(): T {
        return contextObj.component1()
    }
    override fun setValue(value: T) {
        val setContextValue = contextObj.component2()
        setContextValue(value)
    }
}

private class DelegateBufferedContext<T, K: Delegator<T>>(
    context: Context<K>
): BufferedContext<T>() {
    private val contextObj = useContext(context)
    private var delegateValue by contextObj
    override fun getValue(): T {
        return delegateValue
    }
    override fun setValue(value: T) {
        delegateValue = value
    }
}