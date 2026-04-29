/*
 * Copyright 2020 The Android Open Source Project
 * Copyright 2025-2026 Nishant Mishra (Tomato port)
 */

package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_avatar_colleague
import tomato.shared.generated.resources.jetchat_profile_bio
import tomato.shared.generated.resources.jetchat_profile_change_avatar
import tomato.shared.generated.resources.jetchat_profile_save

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun JetchatProfileScreenTomato(
    profileNavId: String,
    currentUserId: Long,
    serverBaseUrl: String,
    authToken: String,
    gateway: JetchatServerGatewayTomato,
    contentPadding: PaddingValues,
    onBackPressed: () -> Unit,
    onProfileUpdated: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val targetUserId = remember(profileNavId, currentUserId) {
        when {
            profileNavId == JETCHAT_AUTHOR_ME -> currentUserId
            else -> profileNavId.toLongOrNull() ?: currentUserId
        }
    }
    val isSelf = targetUserId == currentUserId

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var profile by remember { mutableStateOf<JetchatUserProfileTomato?>(null) }
    var bioDraft by remember { mutableStateOf("") }

    val pickAvatar = rememberJetchatImagePickerLauncher { uri ->
        scope.launch {
            val uploaded = gateway.uploadMedia(authToken, uri).getOrElse {
                error = it.message
                return@launch
            }
            val updated = gateway.updateMyProfile(authToken, bio = null, avatarMediaUrl = uploaded).getOrElse {
                error = it.message
                return@launch
            }
            profile = updated
            onProfileUpdated()
        }
    }

    LaunchedEffect(profileNavId, targetUserId, authToken) {
        loading = true
        error = null
        val res = if (isSelf) {
            gateway.getMyProfile(authToken)
        } else {
            gateway.getUserProfile(authToken, targetUserId)
        }
        res.onSuccess {
            profile = it
            bioDraft = it.bio
            loading = false
        }.onFailure {
            error = it.message
            loading = false
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            JetchatAppBarTomato(
                scrollBehavior = null,
                title = { Text(profile?.username ?: profileNavId) },
                onNavIconPressed = onBackPressed,
                actions = {},
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                when {
                    loading -> {
                        Spacer(Modifier.height(48.dp))
                        CircularProgressIndicator()
                    }

                    error != null -> {
                        Spacer(Modifier.height(48.dp))
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                    }

                    profile != null -> {
                        val p = profile!!
                        val avatarUri = p.avatarMediaUrl.trim().takeIf { it.isNotEmpty() }?.let { path ->
                            if (path.startsWith("http://") || path.startsWith("https://")) {
                                "$path#bearer=$authToken"
                            } else {
                                "${serverBaseUrl.trimEnd('/')}$path#bearer=$authToken"
                            }
                        }
                        if (avatarUri != null) {
                            JetchatPlatformImageTomato(
                                uri = avatarUri,
                                contentDescription = p.username,
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .size(120.dp)
                                    .clip(CircleShape),
                            )
                        } else {
                            Image(
                                painter = painterResource(Res.drawable.jetchat_avatar_colleague),
                                contentDescription = p.username,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .size(120.dp)
                                    .clip(CircleShape),
                            )
                        }
                        Text(
                            text = p.username,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                        if (isSelf) {
                            Button(
                                onClick = pickAvatar,
                                modifier = Modifier.padding(top = 8.dp),
                            ) {
                                Text(stringResource(Res.string.jetchat_profile_change_avatar))
                            }
                            OutlinedTextField(
                                value = bioDraft,
                                onValueChange = { bioDraft = it },
                                label = { Text(stringResource(Res.string.jetchat_profile_bio)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                minLines = 2,
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        val updated = gateway.updateMyProfile(
                                            authToken,
                                            bio = bioDraft,
                                            avatarMediaUrl = null,
                                        ).getOrElse {
                                            error = it.message
                                            return@launch
                                        }
                                        profile = updated
                                        onProfileUpdated()
                                    }
                                },
                                modifier = Modifier.padding(top = 12.dp),
                            ) {
                                Text(stringResource(Res.string.jetchat_profile_save))
                            }
                        } else {
                            Text(
                                text = p.bio.ifBlank { "—" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
