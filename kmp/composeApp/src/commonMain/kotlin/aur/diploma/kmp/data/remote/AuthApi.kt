package aur.diploma.kmp.data.remote

import aur.diploma.kmp.data.model.LoginRequest
import aur.diploma.kmp.data.model.LoginResponse
import aur.diploma.kmp.data.model.UserProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApi(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("/api/auth/login") {
            setBody(request)
        }.body()
    }

    suspend fun me(): UserProfileResponse {
        return client.get("/api/auth/me").body()
    }
}
