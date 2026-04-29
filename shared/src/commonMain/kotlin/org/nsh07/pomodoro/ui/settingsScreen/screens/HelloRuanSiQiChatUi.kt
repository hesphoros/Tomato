/*
 * Copyright (c) 2020 The Android Open Source Project
 * Copyright (c) 2025-2026 Nishant Mishra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Thin entry for JetChat [ConversationContent](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/Conversation.kt).
 */

package org.nsh07.pomodoro.ui.settingsScreen.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatConversationContentTomato
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatConversationUiState

@Composable
fun HelloRuanSiQiJetChatContent(
    onNavIconPressed: () -> Unit,
    navigateToProfile: (String) -> Unit,
    uiState: JetchatConversationUiState,
    currentUserId: Long,
    onSendTextMessage: (String) -> Unit,
    onSendImageMessage: (String) -> Unit,
    onSendStickerMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClearChatHistory: (() -> Unit)? = null,
) {
    JetchatConversationContentTomato(
        uiState = uiState,
        navigateToProfile = { userId ->
            navigateToProfile(userId)
        },
        modifier = modifier,
        onNavIconPressed = onNavIconPressed,
        currentUserId = currentUserId,
        onSendTextMessage = onSendTextMessage,
        onSendImageMessage = onSendImageMessage,
        onSendStickerMessage = onSendStickerMessage,
        onClearChatHistory = onClearChatHistory,
    )
}
