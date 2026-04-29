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

@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)

package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.nsh07.pomodoro.data.PreferenceRepository
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_attached_image
import tomato.shared.generated.resources.jetchat_channel_members
import tomato.shared.generated.resources.jetchat_clear_chat_history
import tomato.shared.generated.resources.jetchat_ic_info
import tomato.shared.generated.resources.jetchat_ic_search
import tomato.shared.generated.resources.jetchat_info
import tomato.shared.generated.resources.jetchat_now
import tomato.shared.generated.resources.jetchat_search
import tomato.shared.generated.resources.jetchat_today

private const val JETCHAT_LOCAL_STICKERS_PREF_KEY = "jetchat_local_stickers_v1"

/**
 * JetChat [ConversationContent](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/Conversation.kt).
 */
@Composable
fun JetchatConversationContentTomato(
    uiState: JetchatConversationUiState,
    navigateToProfile: (String) -> Unit,
    currentUserId: Long,
    onSendTextMessage: (String) -> Unit,
    onSendImageMessage: (String) -> Unit,
    onSendStickerMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = {},
    onClearChatHistory: (() -> Unit)? = null,
) {
    val preferenceRepository: PreferenceRepository = koinInject()
    val authorMe = JETCHAT_AUTHOR_ME
    val timeNow = stringResource(Res.string.jetchat_now)

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()

    var background by remember { mutableStateOf(Color.Transparent) }
    var borderStroke by remember { mutableStateOf(Color.Transparent) }
    var fullscreenDrawable by remember { mutableStateOf<org.jetbrains.compose.resources.DrawableResource?>(null) }
    var fullscreenLocalUri by remember { mutableStateOf<String?>(null) }
    var localStickerUris by remember { mutableStateOf(emptyList<String>()) }

    androidx.compose.runtime.LaunchedEffect(preferenceRepository) {
        localStickerUris = loadLocalStickersTomato(preferenceRepository)
    }

    val dropLifecycle = remember {
        HelloRuanConversationDropLifecycle(
            onStarted = { borderStroke = Color.Red },
            onEntered = { background = Color.Red.copy(alpha = 0.3f) },
            onExited = { background = Color.Transparent },
            onEnded = {
                background = Color.Transparent
                borderStroke = Color.Transparent
            },
        )
    }

    Scaffold(
        topBar = {
            JetchatChannelNameBarTomato(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                onNavIconPressed = onNavIconPressed,
                scrollBehavior = scrollBehavior,
                onClearChatHistory = onClearChatHistory,
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = background)
                .border(width = 2.dp, color = borderStroke)
                .helloRuanConversationPlainTextDrop(
                    onPlainTextDropped = { dropped ->
                        uiState.addMessage(
                            JetchatMessage(
                                author = authorMe,
                                content = dropped,
                                timestamp = timeNow,
                                profileNavId = authorMe,
                            ),
                        )
                        true
                    },
                    lifecycle = dropLifecycle,
                ),
        ) {
            JetchatMessagesTomato(
                messages = uiState.messages,
                navigateToProfile = navigateToProfile,
                onImageClicked = { drawable ->
                    fullscreenDrawable = drawable
                    fullscreenLocalUri = null
                },
                onLocalImageClicked = { uri ->
                    fullscreenLocalUri = uri
                    fullscreenDrawable = null
                },
                currentUserId = currentUserId,
                modifier = Modifier.weight(1f),
                scrollState = scrollState,
            )
            JetchatUserInputTomato(
                onMessageSent = { content ->
                    onSendTextMessage(content)
                },
                onStickerSent = { sticker ->
                    uiState.addMessage(
                        JetchatMessage(
                            senderId = currentUserId,
                            author = authorMe,
                            content = "",
                            timestamp = timeNow,
                            image = sticker,
                        )
                    )
                },
                onLocalStickerSent = { uri ->
                    val updated = listOf(uri) + localStickerUris.filterNot { it == uri }
                    localStickerUris = updated.take(48)
                    scope.launch {
                        preferenceRepository.saveStringPreference(
                            JETCHAT_LOCAL_STICKERS_PREF_KEY,
                            localStickerUris.joinToString("\n"),
                        )
                    }
                    onSendStickerMessage(uri)
                },
                localStickerUris = localStickerUris,
                onLocalImageSent = { uri ->
                    onSendImageMessage(uri)
                },
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                modifier = Modifier,
            )
        }
    }
    if (fullscreenDrawable != null || fullscreenLocalUri != null) {
        Dialog(
            onDismissRequest = {
                fullscreenDrawable = null
                fullscreenLocalUri = null
            },
        ) {
            Surface(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        fullscreenDrawable = null
                        fullscreenLocalUri = null
                    },
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    fullscreenDrawable?.let { drawable ->
                        Image(
                            painter = painterResource(drawable),
                            contentDescription = stringResource(Res.string.jetchat_attached_image),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                        )
                    }
                    fullscreenLocalUri?.let { uri ->
                        JetchatPlatformImageTomato(
                            uri = uri,
                            contentDescription = stringResource(Res.string.jetchat_attached_image),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JetchatChannelNameBarTomato(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = {},
    onClearChatHistory: (() -> Unit)? = null,
) {
    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
        JetchatFunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }
    var overflowOpen by remember { mutableStateOf(false) }

    JetchatAppBarTomato(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(Res.string.jetchat_channel_members, channelMembers),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        actions = {
            Icon(
                painter = painterResource(Res.drawable.jetchat_ic_search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = { functionalityNotAvailablePopupShown = true })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(Res.string.jetchat_search),
            )
            Box {
                Icon(
                    painter = painterResource(Res.drawable.jetchat_ic_info),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(onClick = { overflowOpen = true })
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .height(24.dp),
                    contentDescription = stringResource(Res.string.jetchat_info),
                )
                DropdownMenu(
                    expanded = overflowOpen,
                    onDismissRequest = { overflowOpen = false },
                    modifier = Modifier.width(200.dp),
                ) {
                    if (onClearChatHistory != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.jetchat_clear_chat_history)) },
                            onClick = {
                                overflowOpen = false
                                onClearChatHistory()
                            },
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.jetchat_info)) },
                            onClick = {
                                overflowOpen = false
                                functionalityNotAvailablePopupShown = true
                            },
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun JetchatMessagesTomato(
    messages: List<JetchatMessage>,
    navigateToProfile: (String) -> Unit,
    onImageClicked: (org.jetbrains.compose.resources.DrawableResource) -> Unit,
    onLocalImageClicked: (String) -> Unit,
    currentUserId: Long,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
        ) {
            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author

                if (index == messages.size - 1) {
                    item { JetchatDayHeaderTomato("20 Aug") }
                } else if (index == 2) {
                    item { JetchatDayHeaderTomato(stringResource(Res.string.jetchat_today)) }
                }

                item {
                    JetchatConversationMessageRowTomato(
                        onAuthorClick = { key -> navigateToProfile(key) },
                        onImageClicked = onImageClicked,
                        onLocalImageClicked = onLocalImageClicked,
                        msg = content,
                        isUserMe = (content.senderId != null && content.senderId == currentUserId) ||
                            content.author == JETCHAT_AUTHOR_ME,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                    )
                }
            }
        }

        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                    scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JetchatJumpToBottomTomato(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun JetchatConversationMessageRowTomato(
    onAuthorClick: (String) -> Unit,
    onImageClicked: (org.jetbrains.compose.resources.DrawableResource) -> Unit,
    onLocalImageClicked: (String) -> Unit,
    msg: JetchatMessage,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors =
        if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthor) {
            val avatarModifier = Modifier
                .clickable(onClick = { onAuthorClick(msg.profileNavId) })
                .padding(horizontal = 16.dp)
                .size(42.dp)
                .border(1.5.dp, borderColor, CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .clip(CircleShape)
                .align(Alignment.Top)
            val remote = msg.authorAvatarUrl
            if (!remote.isNullOrBlank()) {
                JetchatPlatformImageTomato(
                    uri = remote,
                    contentDescription = msg.author,
                    modifier = avatarModifier,
                )
            } else {
                Image(
                    modifier = avatarModifier,
                    painter = painterResource(msg.authorImage),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
        } else {
            Spacer(modifier = Modifier.width(74.dp))
        }
        JetchatAuthorAndTextMessageTomato(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            onImageClicked = onImageClicked,
            onLocalImageClicked = onLocalImageClicked,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f),
        )
    }
}

@Composable
private fun JetchatAuthorAndTextMessageTomato(
    msg: JetchatMessage,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    onImageClicked: (org.jetbrains.compose.resources.DrawableResource) -> Unit,
    onLocalImageClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (isLastMessageByAuthor) {
            JetchatAuthorNameTimestampTomato(msg)
        }
        JetchatChatItemBubbleTomato(
            msg,
            isUserMe,
            authorClicked = authorClicked,
            onImageClicked = onImageClicked,
            onLocalImageClicked = onLocalImageClicked,
        )
        if (isFirstMessageByAuthor) {
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun JetchatAuthorNameTimestampTomato(msg: JetchatMessage) {
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
private fun JetchatDayHeaderTomato(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp),
    ) {
        JetchatDayHeaderLineTomato()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        JetchatDayHeaderLineTomato()
    }
}

@Composable
private fun RowScope.JetchatDayHeaderLineTomato() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
}

@Composable
private fun JetchatChatItemBubbleTomato(
    message: JetchatMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit,
    onImageClicked: (org.jetbrains.compose.resources.DrawableResource) -> Unit,
    onLocalImageClicked: (String) -> Unit,
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column {
        if (message.content.isNotBlank()) {
            Surface(
                color = backgroundBubbleColor,
                shape = ChatBubbleShape,
            ) {
                JetchatClickableMessageTomato(
                    message = message,
                    isUserMe = isUserMe,
                    authorClicked = authorClicked,
                )
            }
        }

        message.image?.let { img ->
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = backgroundBubbleColor,
                shape = ChatBubbleShape,
            ) {
                Image(
                    painter = painterResource(img),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(160.dp)
                        .clickable { onImageClicked(img) },
                    contentDescription = stringResource(Res.string.jetchat_attached_image),
                )
            }
        }
        message.localImageUri?.let { imageUri ->
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = backgroundBubbleColor,
                shape = ChatBubbleShape,
            ) {
                JetchatPlatformImageTomato(
                    uri = imageUri,
                    modifier = Modifier
                        .size(160.dp)
                        .clickable { onLocalImageClicked(imageUri) },
                    contentDescription = stringResource(Res.string.jetchat_attached_image),
                )
            }
        }
    }
}

@Composable
private fun JetchatClickableMessageTomato(
    message: JetchatMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = message.content,
        primary = isUserMe,
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> authorClicked(annotation.item)
                        else -> Unit
                    }
                }
        },
    )
}

private val JumpToBottomThreshold = 56.dp

/** Same literal author id as JetChat `strings.xml` `author_me` ("me"). */
internal const val JETCHAT_AUTHOR_ME: String = "me"

private suspend fun loadLocalStickersTomato(
    preferenceRepository: PreferenceRepository,
): List<String> {
    val raw = preferenceRepository.getStringPreference(JETCHAT_LOCAL_STICKERS_PREF_KEY)
        ?: return emptyList()
    return raw
        .split("\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
        .take(48)
}
