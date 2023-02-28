package externals.reactCookie

import externals.universalCookie.CookieMap
import externals.universalCookie.Cookies
import react.PropsWithChildren

external interface ReactCookieProps: PropsWithChildren {
    var cookies: Cookies?
    var allCookies: CookieMap?
}