package reactUtils

import react.StateInstance


// Because react decides to update components based on referential equality of their props,
//  objects have to be reconstructed (which changes the reference address) in order to force
//  update the object of which has been modified

class ReactBox<T> private constructor(
    val value: T,
) {
    internal var onUpdate: ((ReactBox<T>) -> Unit)? = null

    fun update(build: T.() -> T): ReactBox<T> {
        val newBox = ReactBox(value.build())
        onUpdate?.also{ it(newBox) }
        return newBox
    }

    fun update(): ReactBox<T> {
        val newBox = ReactBox(value)
        onUpdate?.also{ it(newBox) }
        return newBox
    }

    companion object {
        fun <T> useState(default: T): StateInstance<ReactBox<T>> {
            val state = react.useState(ReactBox(default))
            state.component1().onUpdate = {
                state.component2()(it)
            }
            return state
        }
        fun <T> useState(): StateInstance<ReactBox<T?>> {
            val state = react.useState<ReactBox<T?>>(ReactBox(null))
            state.component1().onUpdate = {
                state.component2()(it)
            }
            return state
        }
    }
}