package aur.diploma.kmp.data.remote

import android.content.Context
import android.content.SharedPreferences

private var appContext: Context? = null

fun initTokenStorage(context: Context) {
    appContext = context.applicationContext
}

actual class TokenStorage actual constructor() {
    private val prefs: SharedPreferences?
        get() = appContext?.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    actual fun getToken(): String? {
        return prefs?.getString("jwt_token", null)
    }

    actual fun saveToken(token: String) {
        prefs?.edit()?.putString("jwt_token", token)?.apply()
    }

    actual fun clearToken() {
        prefs?.edit()?.remove("jwt_token")?.apply()
    }
}
