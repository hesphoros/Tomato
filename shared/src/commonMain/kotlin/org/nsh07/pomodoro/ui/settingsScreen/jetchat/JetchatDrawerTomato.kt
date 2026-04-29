/*
 * Copyright (c) 2020 The Android Open Source Project
 * Copyright (c) 2025-2026 Nishant Mishra (port for Tomato)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 */

package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_logo
import tomato.shared.generated.resources.jetchat_drawer_chats
import tomato.shared.generated.resources.jetchat_drawer_add_friend
import tomato.shared.generated.resources.jetchat_drawer_recent_profiles
import tomato.shared.generated.resources.jetchat_drawer_settings
import tomato.shared.generated.resources.jetchat_drawer_add_widget
import tomato.shared.generated.resources.mobile_text
import tomato.shared.generated.resources.globe

/**
 * Drawer content matching [JetchatDrawerContent](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/components/JetchatDrawer.kt)
 * (widget pin and Android-only APIs omitted for Tomato/CMP).
 */
@Composable
fun JetchatDrawerTomato(
    selectedMenu: String,
    onChatClicked: (String) -> Unit,
    onContactClicked: (String) -> Unit,
    contacts: List<JetchatContactTomato>,
    mediaBaseUrl: String,
    authToken: String,
    onAddFriendClicked: () -> Unit,
    onServerConfigClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onAddWidgetResult: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val widgetPinState = rememberJetchatWidgetPinState()
    Column(modifier = modifier) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        DrawerHeaderTomato()
        DividerItemTomato()
        DrawerItemHeaderTomato(stringResource(Res.string.jetchat_drawer_chats))
        ChatItemTomato("composers", selectedMenu == "composers") { onChatClicked("composers") }
        ChatItemTomato("droidcon-nyc", selectedMenu == "droidcon-nyc") { onChatClicked("droidcon-nyc") }
        DividerItemTomato(Modifier.padding(horizontal = 28.dp))
        DrawerItemHeaderTomato(stringResource(Res.string.jetchat_drawer_recent_profiles))
        contacts.forEach { contact ->
            val remoteAvatar = contact.avatarMediaUrl?.takeIf { it.isNotBlank() }?.let { path ->
                val full = if (path.startsWith("http://") || path.startsWith("https://")) {
                    path
                } else {
                    "${mediaBaseUrl.trimEnd('/')}$path"
                }
                "$full#bearer=$authToken"
            }
            ProfileItemTomato(
                text = contact.displayName,
                selected = selectedMenu == contact.id,
                image = contact.avatar,
                remoteAvatarUri = remoteAvatar,
                onClick = { onContactClicked(contact.id) }
            )
        }
        AddFriendItemTomato(onAddFriendClicked)
        AddServerConfigItemTomato(onServerConfigClicked)
        AddLogoutItemTomato(onLogoutClicked)
        if (widgetPinState.isSupported) {
            DividerItemTomato(Modifier.padding(horizontal = 28.dp))
            DrawerItemHeaderTomato(stringResource(Res.string.jetchat_drawer_settings))
            WidgetDiscoverabilityTomato(
                onAddWidget = { onAddWidgetResult(widgetPinState.requestPin()) },
            )
        }
    }
}

@Composable
private fun AddFriendItemTomato(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.mobile_text),
            tint = colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null,
        )
        Text(
            text = stringResource(Res.string.jetchat_drawer_add_friend),
            style = typography.bodyMedium,
            color = colorScheme.primary,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun AddServerConfigItemTomato(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.globe),
            tint = colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null,
        )
        Text(
            text = "Server Connection",
            style = typography.bodyMedium,
            color = colorScheme.primary,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun AddLogoutItemTomato(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.mobile_text),
            tint = colorScheme.error,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null,
        )
        Text(
            text = "Logout",
            style = typography.bodyMedium,
            color = colorScheme.error,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun DrawerHeaderTomato() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        JetchatNavIconTomato(
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Image(
            painter = painterResource(Res.drawable.jetchat_logo),
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun DrawerItemHeaderTomato(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = typography.bodySmall,
            color = colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ChatItemTomato(text: String, selected: Boolean, onChatClicked: () -> Unit) {
    val background = if (selected) {
        Modifier.background(colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = onChatClicked),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconTint = if (selected) colorScheme.primary else colorScheme.onSurfaceVariant
        Icon(
            painter = painterResource(Res.drawable.mobile_text),
            tint = iconTint,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null,
        )
        Text(
            text = text,
            style = typography.bodyMedium,
            color = if (selected) colorScheme.primary else colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun ProfileItemTomato(
    text: String,
    selected: Boolean,
    image: org.jetbrains.compose.resources.DrawableResource,
    remoteAvatarUri: String?,
    onClick: () -> Unit,
) {
    val background = if (selected) {
        Modifier.background(colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!remoteAvatarUri.isNullOrBlank()) {
            JetchatPlatformImageTomato(
                uri = remoteAvatarUri,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .size(24.dp)
                    .clip(CircleShape),
            )
        } else {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .size(24.dp)
                    .clip(CircleShape),
            )
        }
        Text(
            text = text,
            style = typography.bodyMedium,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
fun DividerItemTomato(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = colorScheme.onSurface.copy(alpha = 0.12f),
    )
}

@Composable
private fun WidgetDiscoverabilityTomato(onAddWidget: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = onAddWidget),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(Res.string.jetchat_drawer_add_widget),
            style = typography.bodyMedium,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}
