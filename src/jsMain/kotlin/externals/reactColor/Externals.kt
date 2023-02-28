
@file:JsNonModule
@file:JsModule("react-color/")
package externals.reactColor

import org.w3c.dom.events.Event
import react.ComponentClass
import react.FC
import react.Props


external interface BaseInputColorProps: Props {
    var color: BaseColor
    var onChange: (ColorOptions, Event) -> Unit
    var onChangeComplete: (ColorOptions, Event) -> Unit
}

external val AlphaPicker: FC<BaseInputColorProps>
external val BlockPicker: FC<BaseInputColorProps>
external val CirclePicker: FC<BaseInputColorProps>
external val ChromePicker: FC<BaseInputColorProps>
external val CompactPicker: FC<BaseInputColorProps>
external val GithubPicker: FC<BaseInputColorProps>
external val HuePicker: FC<BaseInputColorProps>
external val MaterialPicker: FC<BaseInputColorProps>
external val PhotoshopPicker: FC<BaseInputColorProps>
external val SketchPicker: FC<BaseInputColorProps>
external val SliderPicker: FC<BaseInputColorProps>
external val SwatchesPicker: FC<BaseInputColorProps>
external val TwitterPicker: FC<BaseInputColorProps>
external val GooglePicker: FC<BaseInputColorProps>