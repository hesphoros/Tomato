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

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.arrow_down
import tomato.shared.generated.resources.jetchat_jump_to_bottom

/**
 * JetChat [JumpToBottom](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/JumpToBottom.kt).
 */
private enum class JumpVisibility {
    VISIBLE,
    GONE,
}

@Composable
fun JetchatJumpToBottomTomato(enabled: Boolean, onClicked: () -> Unit, modifier: Modifier = Modifier) {
    val transition = updateTransition(
        if (enabled) JumpVisibility.VISIBLE else JumpVisibility.GONE,
        label = "JumpToBottom visibility animation",
    )
    val bottomOffset by transition.animateDp(label = "JumpToBottom offset animation") {
        if (it == JumpVisibility.GONE) {
            (-32).dp
        } else {
            32.dp
        }
    }
    if (bottomOffset > 0.dp) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.arrow_down),
                    modifier = Modifier.height(18.dp),
                    contentDescription = null,
                )
            },
            text = {
                Text(text = stringResource(Res.string.jetchat_jump_to_bottom))
            },
            onClick = onClicked,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .offset(x = 0.dp, y = -bottomOffset)
                .height(36.dp),
        )
    }
}
