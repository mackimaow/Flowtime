package externalAdditions.mui

import mui.material.BoxProps


var BoxProps.contentCentered: Boolean
    get() {
        return this.asDynamic().display == "flex" &&
            this.asDynamic().justifyContent == "center" &&
            this.asDynamic().alignItems == "center"
    }
    set(value) {
        if (value) {
            this.asDynamic().display = "flex"
            this.asDynamic().justifyContent = "center"
            this.asDynamic().alignItems = "center"
        }
    }