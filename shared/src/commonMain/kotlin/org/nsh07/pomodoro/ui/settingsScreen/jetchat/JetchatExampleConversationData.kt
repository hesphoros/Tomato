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

import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_sticker

/**
 * JetChat [initialMessages](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/data/FakeData.kt)
 * and [EMOJIS](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/data/FakeData.kt).
 */
private object JetchatExampleEmojis {
    const val EMOJI_PINK_HEART = "\uD83E\uDE77"
    const val EMOJI_MELTING = "\uD83E\uDEE0"
    const val EMOJI_CLOUDS = "\uD83D\uDE36\u200D\uD83C\uDF2B️"
    const val EMOJI_FLAMINGO = "\uD83E\uDDA9"
    const val EMOJI_POINTS = " \uD83D\uDC49"
}

val jetchatExampleInitialMessages: List<JetchatMessage> = listOf(
    JetchatMessage("me", "Check it out!", "8:07 PM"),
    JetchatMessage(
        "me",
        "Thank you!${JetchatExampleEmojis.EMOJI_PINK_HEART}",
        "8:06 PM",
        image = Res.drawable.jetchat_sticker,
    ),
    JetchatMessage("Taylor Brooks", "You can use all the same stuff", "8:05 PM"),
    JetchatMessage(
        "Taylor Brooks",
        "@aliconors Take a look at the `Flow.collectAsStateWithLifecycle()` APIs",
        "8:05 PM",
    ),
    JetchatMessage(
        "John Glenn",
        "Compose newbie as well ${JetchatExampleEmojis.EMOJI_FLAMINGO}, have you looked at the JetNews sample? " +
            "Most blog posts end up out of date pretty fast but this sample is always up to " +
            "date and deals with async data loading (it's faked but the same idea " +
            "applies)${JetchatExampleEmojis.EMOJI_POINTS} https://goo.gle/jetnews",
        "8:04 PM",
    ),
    JetchatMessage(
        "me",
        "Compose newbie: I’ve scourged the internet for tutorials about async data " +
            "loading but haven’t found any good ones ${JetchatExampleEmojis.EMOJI_MELTING} ${JetchatExampleEmojis.EMOJI_CLOUDS}. " +
            "What’s the recommended way to load async data and emit composable widgets?",
        "8:03 PM",
    ),
    JetchatMessage(
        "Shangeeth Sivan",
        "Does anyone know about Glance Widgets its the new way to build widgets in Android!",
        "8:08 PM",
    ),
    JetchatMessage(
        "Taylor Brooks",
        "Wow! I never knew about Glance Widgets when was this added to the android ecosystem",
        "8:10 PM",
    ),
    JetchatMessage("John Glenn", "Yeah its seems to be pretty new!", "8:12 PM"),
)

fun jetchatExampleConversationUiState(
    channelName: String,
    channelMembers: Int,
): JetchatConversationUiState = JetchatConversationUiState(
    channelName = channelName,
    channelMembers = channelMembers,
    initialMessages = jetchatExampleInitialMessages,
)

fun jetchatInitialMessagesForContact(contactDisplayName: String): List<JetchatMessage> {
    val directMessages = jetchatExampleInitialMessages.filter {
        it.author == "me" || it.author == contactDisplayName
    }
    if (directMessages.isNotEmpty()) return directMessages
    return listOf(
        JetchatMessage(
            author = contactDisplayName,
            content = "Hi! Nice to connect with you on Tomato.",
            timestamp = "Now",
        ),
    )
}
