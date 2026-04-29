/*
 * Copyright (c) 2025-2026 Nishant Mishra
 *
 * This file is part of Tomato - a minimalist pomodoro timer for Android.
 *
 * Tomato is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tomato is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tomato.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.ui.Modifier

/**
 * Optional callbacks mirroring JetChat [ConversationContent] drag highlight
 * ([Conversation.kt](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/Conversation.kt)).
 */
class HelloRuanConversationDropLifecycle(
    val onStarted: () -> Unit = {},
    val onEntered: () -> Unit = {},
    val onExited: () -> Unit = {},
    val onEnded: () -> Unit = {},
)

/** Android: plain-text drag-and-drop + lifecycle highlight. Else: no-op. */
expect fun Modifier.helloRuanConversationPlainTextDrop(
    onPlainTextDropped: (String) -> Boolean,
    lifecycle: HelloRuanConversationDropLifecycle = HelloRuanConversationDropLifecycle(),
): Modifier
