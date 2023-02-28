@file:JsModule("react-cookie/")
@file:JsNonModule
package externals.reactCookie

external fun <String, U: CookiesHandle> useCookies(
    dependencies: List<String>?
): CookiesHandlers<String, U>

