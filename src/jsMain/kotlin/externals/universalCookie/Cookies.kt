@file:JsModule("universal-cookie")
@file:JsNonModule
package externals.universalCookie

@JsName("default")
external class Cookies(
    cookies: Any? = definedExternally,
    options: CookieParseOptions? = definedExternally
) {

    fun <T> get(name: String, options: CookieGetOptions?): T

    fun <T> get(
        name: String,
        options: CookieGetOptions = definedExternally,
        parseOptions: CookieParseOptions?
    ): T

    fun <T> getAll(options: CookieGetOptions?): T
    fun <T> getAll(
        options: CookieGetOptions = definedExternally,
        parseOptions: CookieParseOptions?
    ): T

    fun set(
        name: String,
        value: Cookie,
        options: CookieSetOptions?
    )
    fun remove(
        name: String,
        options: CookieSetOptions?
    )
    fun addChangeListener(
        callback: CookieChangeListener
    )
    fun removeChangeListener(
        callback: CookieChangeListener
    )
}