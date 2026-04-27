package aur.diploma.kmp.data.remote

import platform.Foundation.NSUserDefaults

actual class TokenStorage actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getToken(): String? {
        return defaults.stringForKey("jwt_token")
    }

    actual fun saveToken(token: String) {
        defaults.setObject(token, forKey = "jwt_token")
    }

    actual fun clearToken() {
        defaults.removeObjectForKey("jwt_token")
    }
}
