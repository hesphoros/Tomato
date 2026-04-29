package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun JetchatPlatformImageTomato(
    uri: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)
