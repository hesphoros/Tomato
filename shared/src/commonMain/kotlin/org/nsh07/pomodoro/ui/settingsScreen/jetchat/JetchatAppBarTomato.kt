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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.ic_jetchat_back
import tomato.shared.generated.resources.ic_jetchat_front
import tomato.shared.generated.resources.jetchat_navigation_drawer_open

/**
 * [JetchatAppBar](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/components/JetchatAppBar.kt)
 * layout: [CenterAlignedTopAppBar] + stacked JetChat mark as navigation icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JetchatAppBarTomato(
    scrollBehavior: TopAppBarScrollBehavior?,
    onNavIconPressed: () -> Unit,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        actions = actions,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            JetchatNavIconTomato(
                contentDescription = stringResource(Res.string.jetchat_navigation_drawer_open),
                modifier = Modifier
                    .size(64.dp)
                    .clickable(onClick = onNavIconPressed)
                    .padding(16.dp),
            )
        },
    )
}

@Composable
internal fun JetchatNavIconTomato(contentDescription: String?, modifier: Modifier = Modifier) {
    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }
    Box(modifier = modifier.then(semanticsModifier)) {
        Icon(
            painter = painterResource(Res.drawable.ic_jetchat_back),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer,
        )
        Icon(
            painter = painterResource(Res.drawable.ic_jetchat_front),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}
