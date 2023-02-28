package externalAdditions.reactCookie

import externals.reactCookie.*
import externals.universalCookie.CookieSetOptions
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty

fun useCookies(
    dependencies: List<String>?
) = useCookies<String, CookiesHandle>(dependencies)

inline fun <reified K> CookiesHandlers<String, CookiesHandle>.get(
    name: String
): CookieHandlers<K> {
    val serializer = Json.serializersModule.serializer<K>()
    return CookieHandlers(
        name,
        this,
        {
            "'${Json.encodeToString(serializer, it)}'"
        },
        {
            Json.decodeFromString(serializer, it.substring(1, it.length - 1))
        }
    )
}

data class SetCookieConfig<T>(
    var cookie: T? = null,
    var options: CookieSetOptions? = null
)

// single cookie, multiple handlers
class CookieHandlers<T>(
    private val name: String,
    private val cookiesHandlers: CookiesHandlers<String, *>,
    private val serializer: (T) -> String,
    private val deserializer: (String) -> T,
    private val options: CookieSetOptions? = null
) {

    fun withOptions(options: CookieSetOptions): CookieHandlers<T> {
        return CookieHandlers(
            name, cookiesHandlers, serializer, deserializer, options
        )
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val (cookies) = cookiesHandlers
        val cookie = cookies.get(name) as? String
        return cookie?.let{ deserializer(it) }
    }

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T?
    ) {
        val (_, setCookie, removeCookie) = cookiesHandlers
        value?.also {
            setCookie(name, serializer(it), options)
        } ?: removeCookie(name, options)
    }
}