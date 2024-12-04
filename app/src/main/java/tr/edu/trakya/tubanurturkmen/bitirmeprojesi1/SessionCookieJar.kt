import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SessionCookieJar : CookieJar {
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val key = url.host() // Host bilgisi alınıyor
        if (cookieStore[key] == null) {
            cookieStore[key] = cookies.toMutableList()
        } else {
            cookieStore[key]?.addAll(cookies)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val key = url.host() // Host bilgisi alınıyor
        val cookies = cookieStore[key] ?: return emptyList()

        // Süresi dolmuş cookie'leri filtreleme
        val currentTime = System.currentTimeMillis()
        val validCookies = cookies.filter { isValidCookie(it, currentTime) }

        // Süresi dolmuş cookie'leri temizleme
        cookieStore[key] = validCookies.toMutableList()

        return validCookies
    }

    private fun isValidCookie(cookie: Cookie, currentTime: Long): Boolean {
        // Cookie'nin süresini kontrol etme
        return cookie.expiresAt() > currentTime
    }
}