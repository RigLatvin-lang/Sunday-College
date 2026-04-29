package aur.diploma.kmp.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aur.diploma.kmp.ui.components.ErrorMessage
import aur.diploma.kmp.ui.navigation.AppRole
import aur.diploma.kmp.ui.theme.Background
import aur.diploma.kmp.ui.theme.Border
import aur.diploma.kmp.ui.theme.InputBackground
import aur.diploma.kmp.ui.theme.Primary
import aur.diploma.kmp.ui.theme.Surface
import aur.diploma.kmp.ui.theme.TextMuted
import aur.diploma.kmp.ui.theme.TextPrimary
import aur.diploma.kmp.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (AppRole) -> Unit,
    onScheduleClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loginResult, state.appRole) {
        val appRole = state.appRole
        if (state.loginResult != null && appRole != null) {
            onLoginSuccess(appRole)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(0.92f)
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Вход в систему",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Вход для преподавателя, родителя и студента",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(28.dp))

            if (state.error != null) {
                ErrorMessage(message = state.error!!)
                Spacer(Modifier.height(16.dp))
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ЛОГИН",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.login,
                    onValueChange = viewModel::onLoginChanged,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    placeholder = { Text("Введите логин", color = TextMuted, fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = InputBackground,
                        focusedContainerColor = InputBackground,
                        unfocusedBorderColor = Border,
                        focusedBorderColor = Primary,
                        cursorColor = Primary
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ПАРОЛЬ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text("Введите пароль", color = TextMuted, fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = InputBackground,
                        focusedContainerColor = InputBackground,
                        unfocusedBorderColor = Border,
                        focusedBorderColor = Primary,
                        cursorColor = Primary
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = viewModel::login,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Surface,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Войти",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onScheduleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
            ) {
                Text(
                    text = "Расписание",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
