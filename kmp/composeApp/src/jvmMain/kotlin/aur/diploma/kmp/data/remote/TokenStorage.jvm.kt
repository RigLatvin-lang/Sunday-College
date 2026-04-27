package aur.diploma.kmp.data.remote

import java.util.prefs.Preferences

actual class TokenStorage actual constructor() {
    private val prefs = Preferences.userNodeForPackage(TokenStorage::class.java)

    actual fun getToken(): String? {
        return prefs.get("jwt_token", null)
    }

    actual fun saveToken(token: String) {
        prefs.put("jwt_token", token)
    }

    actual fun clearToken() {
        prefs.remove("jwt_token")
    }
}
