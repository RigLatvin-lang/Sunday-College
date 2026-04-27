package aur.diploma.kmp

import androidx.compose.ui.window.ComposeUIViewController
import aur.diploma.kmp.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule)
    }
    App()
}
