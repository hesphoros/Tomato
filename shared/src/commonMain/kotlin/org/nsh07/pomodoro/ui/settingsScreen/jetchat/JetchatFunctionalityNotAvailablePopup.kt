/*
 * Copyright 2020 The Android Open Source Project
 * Copyright 2025-2026 Nishant Mishra (Tomato port)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 */

package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_not_available
import tomato.shared.generated.resources.ok

/**
 * JetChat [FunctionalityNotAvailablePopup](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/UiExtras.kt)
 * with localized strings.
 */
@Composable
fun JetchatFunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(Res.string.jetchat_not_available),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.ok))
            }
        },
    )
}
