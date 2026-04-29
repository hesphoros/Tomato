package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.runtime.Composable

@Composable
expect fun rememberJetchatImagePickerLauncher(
    onImagePicked: (String) -> Unit,
): () -> Unit
