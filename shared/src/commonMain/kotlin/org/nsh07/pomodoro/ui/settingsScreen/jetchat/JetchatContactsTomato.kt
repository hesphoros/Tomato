package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.DrawableResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_avatar_ali
import tomato.shared.generated.resources.jetchat_avatar_colleague

@Immutable
data class JetchatContactTomato(
    val id: String,
    val displayName: String,
    val avatar: DrawableResource,
    val bio: String = "",
    /** Server-relative media path, e.g. `/media/2026/04/x.webp` */
    val avatarMediaUrl: String? = null,
)

internal val jetchatDefaultContactsTomato: List<JetchatContactTomato> = listOf(
    JetchatContactTomato(
        id = "me",
        displayName = "Ali Conors",
        avatar = Res.drawable.jetchat_avatar_ali,
    ),
    JetchatContactTomato(
        id = "12345",
        displayName = "Taylor Brooks",
        avatar = Res.drawable.jetchat_avatar_colleague,
    ),
)
