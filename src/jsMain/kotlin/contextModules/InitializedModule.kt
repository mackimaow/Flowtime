package contextModules

import react.FC
import react.PropsWithChildren
import react.createContext


val InitializedContext = createContext(false)

external interface InitializedModuleProps: PropsWithChildren {
    var initialized: Boolean?
}

val InitializedModule = FC<InitializedModuleProps> { props ->
    InitializedContext(props.initialized ?: false) {
        + props.children
    }
}