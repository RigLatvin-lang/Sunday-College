package aur.diploma.kmp.data.remote

import kotlinx.browser.localStorage

actual class TokenStorage actual constructor() {
    actual fun getToken(): String? {
        return localStorage.getItem("jwt_token")
    }

    actual fun saveToken(token: String) {
        localStorage.setItem("jwt_token", token)
    }

    actual fun clearToken() {
        localStorage.removeItem("jwt_token")
    }
}
