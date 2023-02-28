package externals.reactCookie

import externals.universalCookie.Cookie
import externals.universalCookie.CookieSetOptions

external interface CookiesHandlers <T, U: CookiesHandle>


inline operator fun <T, U: CookiesHandle> CookiesHandlers<T, U>.component1(
): U =
    this.asDynamic()[0] as U

inline operator fun <T, U: CookiesHandle> CookiesHandlers<T, U>.component2(
): (T, Cookie, CookieSetOptions?) -> Unit =
    this.asDynamic()[1] as (T, Cookie, CookieSetOptions?) -> Unit

inline operator fun <T, U: CookiesHandle> CookiesHandlers<T, U>.component3(
): (T, CookieSetOptions?) -> Unit =
    this.asDynamic()[2] as (T, CookieSetOptions?) -> Unit



external interface CookiesHandle

inline fun CookiesHandle.get(name: String): Cookie? {
    return this.asDynamic()[name]
}

