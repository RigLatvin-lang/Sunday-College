package aur.diploma.kmp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val role: String,
    val fullName: String
)

@Serializable
data class UserProfileResponse(
    val id: Long,
    val fullName: String,
    val login: String,
    val role: String
)
