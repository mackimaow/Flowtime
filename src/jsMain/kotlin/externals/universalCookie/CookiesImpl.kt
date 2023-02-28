package externals.universalCookie

external interface CookieMap

inline operator fun CookieMap.get(name: String): Cookie? =
    this.asDynamic()[name]

inline operator fun CookieMap.set(name: String, result: Cookie?) {
    this.asDynamic()[name] = result
}

typealias Cookie = Any