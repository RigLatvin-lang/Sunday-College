package aur.diploma.kmp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupResponse(
    val id: Long,
    val name: String,
    val students: List<StudentResponse> = emptyList()
)
