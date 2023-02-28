package contextModules

import externals.reactCookie.CookiesProvider
import externals.universalCookie.Cookies
import react.*


typealias CookieState = StateInstance<Cookies>

val CookieContext = createContext<CookieState>()


val AppCookieProvider = FC<PropsWithChildren> { props ->

    val state = useState(Cookies())
    var (cookies) = state

    // exposes theme context to our app components
    CookieContext(state) {

        // exposes theme context to mui app components

        CookiesProvider {
            cookies = cookies
            +props.children
        }
    }
}