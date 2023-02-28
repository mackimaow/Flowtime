@file:JsModule("react-transition-group")
@file:JsNonModule
package externalAdditions.reactTransitionGroup

import react.ElementType
import react.FC
import react.PropsWithChildren
import react.ReactNode

external interface TransitionGroupProps: PropsWithChildren {
    var component: ElementType<*>
}

external val TransitionGroup: FC<TransitionGroupProps>