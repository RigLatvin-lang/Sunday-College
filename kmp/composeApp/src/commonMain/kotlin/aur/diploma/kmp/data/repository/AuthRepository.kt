package aur.diploma.kmp.data.repository

import aur.diploma.kmp.data.model.LoginRequest
import aur.diploma.kmp.data.model.LoginResponse
import aur.diploma.kmp.data.model.UserProfileResponse
import aur.diploma.kmp.data.remote.AuthApi
import aur.diploma.kmp.data.remote.TokenStorage

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {
    val isLoggedIn: Boolean get() = tokenStorage.getToken() != null

    suspend fun login(login: String, password: String): LoginResponse {
        return try {
            val response = authApi.login(LoginRequest(login, password))
            tokenStorage.saveToken(response.token)
            response
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun me(): UserProfileResponse {
        return authApi.me()
    }

    suspend fun getCurrentRole(): String? {
        if (!isLoggedIn) return null
        return try {
            me().role
        } catch (_: Exception) {
            logout()
            null
        }
    }

    fun logout() {
        tokenStorage.clearToken()
    }
}
