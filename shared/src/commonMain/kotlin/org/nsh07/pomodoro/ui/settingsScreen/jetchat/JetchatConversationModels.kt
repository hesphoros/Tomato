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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList
import org.jetbrains.compose.resources.DrawableResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_avatar_ali
import tomato.shared.generated.resources.jetchat_avatar_colleague

/**
 * JetChat [ConversationUiState](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/ConversationUiState.kt)
 * and [Message](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/ConversationUiState.kt).
 */
class JetchatConversationUiState(
    val channelName: String,
    val channelMembers: Int,
    initialMessages: List<JetchatMessage>,
) {
    private val _messages: MutableList<JetchatMessage> = initialMessages.toMutableStateList()
    val messages: List<JetchatMessage> = _messages

    fun addMessage(msg: JetchatMessage) {
        _messages.add(0, msg)
    }

    fun replaceAllMessages(messages: List<JetchatMessage>) {
        _messages.clear()
        _messages.addAll(messages)
    }
}

@Immutable
data class JetchatMessage(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: DrawableResource? = null,
    val localImageUri: String? = null,
    val serverMessageId: Long? = null,
    val senderId: Long? = null,
    /** Stable id for profile screen: `"me"` or contact id string. */
    val profileNavId: String = author,
    /** Optional remote avatar URL (may include `#bearer=` for authenticated media). */
    val authorAvatarUrl: String? = null,
    val authorImage: DrawableResource = if (author == "me") {
        Res.drawable.jetchat_avatar_ali
    } else {
        Res.drawable.jetchat_avatar_colleague
    },
)
