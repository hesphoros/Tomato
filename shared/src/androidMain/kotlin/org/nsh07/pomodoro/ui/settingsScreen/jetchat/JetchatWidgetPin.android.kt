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

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberJetchatWidgetPinState(): JetchatWidgetPinState {
    val context = LocalContext.current
    val appWidgetManager = remember(context) { AppWidgetManager.getInstance(context) }
    val isSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        appWidgetManager.isRequestPinAppWidgetSupported

    val requestPin = remember(context, appWidgetManager, isSupported) {
        {
            if (!isSupported) return@remember false
            val provider = ComponentName(
                context,
                // Receiver class lives in androidApp module; keep string-based to avoid module dependency.
                "org.nsh07.pomodoro.widget.TimerWidgetReceiver",
            )
            runCatching {
                appWidgetManager.requestPinAppWidget(provider, null, null)
            }.getOrDefault(false)
        }
    }

    return JetchatWidgetPinState(isSupported = isSupported, requestPin = requestPin)
}

