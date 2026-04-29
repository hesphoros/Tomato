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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class JetchatWidgetPinState(
    val isSupported: Boolean,
    val requestPin: () -> Boolean,
)

/** Android: backed by [AppWidgetManager.requestPinAppWidget]. Other platforms: unsupported no-op. */
@Composable
expect fun rememberJetchatWidgetPinState(): JetchatWidgetPinState

