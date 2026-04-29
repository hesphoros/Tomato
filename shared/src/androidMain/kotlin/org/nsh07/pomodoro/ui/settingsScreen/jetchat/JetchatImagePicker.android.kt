package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberJetchatImagePickerLauncher(
    onImagePicked: (String) -> Unit,
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { onImagePicked(it.toString()) }
    }
    return { launcher.launch(arrayOf("image/*")) }
}
