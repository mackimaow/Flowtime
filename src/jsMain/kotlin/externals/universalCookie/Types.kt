// universal-cookies actually doesn't export these types,
// so we just need to add them as external interfaces
package externals.universalCookie

import io.github.mackimaow.kotlin.union.Union
import io.github.mackimaow.kotlin.union.UnionOptions
import kotlin.js.Date

external interface CookieGetOptions {
    var doNotParse: Boolean?
}

external interface CookieSetOptions {
    var path: String?
    var expires: Date?
    var maxAge: Number?
    var domain: String?
    var secure: Boolean?
    var httpOnly: Boolean?
    var sameSite: Union<SameSiteOptions>?
    var encode: ((String) -> String)?
}

external interface CookieChangeOptions {
    var name: String
    var value: Any?
    var options: CookieSetOptions?
}

external class CookieParseOptions {
    var decode: (String) -> String
}

object SameSiteOptions: UnionOptions<SameSiteOptions>({SameSiteOptions}) {
    val BOOLEAN = option<Boolean>()
    val NONE = literal("none")
    val LAX = literal("lax")
    val STRICT = literal("strict")
}
