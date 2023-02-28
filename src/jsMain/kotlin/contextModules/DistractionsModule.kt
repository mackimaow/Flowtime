package contextModules

import data.Distraction
import data.getAll
import kotlinx.coroutines.launch
import react.*
import reactUtils.ReactBox
import scope

typealias DistractionsState = ReactBox<MutableList<Distraction>>

val DistractionsContext = createContext<DistractionsState>()

val DistractionsProvider = FC<PropsWithChildren> { props ->
    val distractionsBox by ReactBox.useState(mutableListOf<Distraction>())
    val initialized = useContext(InitializedContext)

    useEffect(initialized) {
        if (initialized) {
            scope.launch {
                val items = Distraction.getAll()
                distractionsBox.update {
                    items.toMutableList()
                }
            }
        }
    }

    DistractionsContext(distractionsBox) {
        + props.children
    }
}