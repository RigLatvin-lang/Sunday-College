package aur.diploma.kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform