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

package org.nsh07.pomodoro.ui.settingsScreen.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.nsh07.pomodoro.data.PreferenceRepository
import org.nsh07.pomodoro.ui.mergePaddingValues
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatAuthGatewayTomato
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.ComposeBackHandler
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JETCHAT_AUTHOR_ME
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatContactTomato
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatDrawerTomato
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatConversationUiState
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatMessage
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatNavIconTomato
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatProfileScreenTomato
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.rememberChatInboundDiscreetNotifier
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.rememberJetchatAuthGatewayTomato
import org.nsh07.pomodoro.ui.settingsScreen.jetchat.rememberJetchatServerGatewayTomato
import org.nsh07.pomodoro.ui.theme.CustomColors.listItemColors
import org.nsh07.pomodoro.ui.theme.CustomColors.topBarColors
import org.nsh07.pomodoro.ui.theme.LocalAppFonts
import org.nsh07.pomodoro.ui.theme.TomatoShapeDefaults.PANE_MAX_WIDTH
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.arrow_back
import tomato.shared.generated.resources.back
import tomato.shared.generated.resources.cancel
import tomato.shared.generated.resources.jetchat_login_button
import tomato.shared.generated.resources.jetchat_clear_chat_history
import tomato.shared.generated.resources.jetchat_clear_chat_history_confirm
import tomato.shared.generated.resources.jetchat_empty_contacts_message
import tomato.shared.generated.resources.jetchat_empty_contacts_title
import tomato.shared.generated.resources.jetchat_navigation_drawer_open
import tomato.shared.generated.resources.jetchat_login_error_empty
import tomato.shared.generated.resources.jetchat_login_hint
import tomato.shared.generated.resources.jetchat_login_required
import tomato.shared.generated.resources.jetchat_login_subtitle
import tomato.shared.generated.resources.jetchat_login_title
import tomato.shared.generated.resources.jetchat_not_available
import tomato.shared.generated.resources.jetchat_drawer_add_friend
import tomato.shared.generated.resources.jetchat_widget_pin_failed
import tomato.shared.generated.resources.jetchat_widget_pin_requested
import tomato.shared.generated.resources.ok

private const val GREETING = "Hello RuanSiQi"

/** Hold greeting on screen after entrance animation (ms). */
private const val GreetingHoldMs = 2200L

/** Pause before switching to JetChat-style UI (ms). */
private const val ChatTransitionGapMs = 120L
private const val JETCHAT_LOGIN_STATE_KEY = "jetchat_login_state_v1"
private const val JETCHAT_LOGIN_USER_KEY = "jetchat_login_user_v1"
private const val JETCHAT_LOGIN_TOKEN_KEY = "jetchat_login_token_v1"
private const val JETCHAT_LOGIN_USER_ID_KEY = "jetchat_login_user_id_v1"
private const val JETCHAT_SERVER_BASE_URL_KEY = "jetchat_server_base_url_v1"
private const val JETCHAT_DEFAULT_SERVER_URL = "https://114.55.6.122:8443"

private enum class HelloRuanPhase {
    Greeting,
    Chat
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HelloRuanSiQiScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val motionScheme = motionScheme
    val barColors = topBarColors

    var phase by remember { mutableStateOf(HelloRuanPhase.Greeting) }
    LaunchedEffect(Unit) {
        delay(GreetingHoldMs)
        delay(ChatTransitionGapMs)
        phase = HelloRuanPhase.Chat
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(barColors.containerColor)
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                fadeIn(motionScheme.slowEffectsSpec())
                    .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
            },
            label = "helloRuanPhase",
            modifier = modifier
                .widthIn(max = PANE_MAX_WIDTH)
                .fillMaxSize()
        ) { p ->
            when (p) {
                HelloRuanPhase.Greeting -> Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { },
                            navigationIcon = {
                                FilledTonalIconButton(
                                    onClick = onBack,
                                    shapes = IconButtonDefaults.shapes(),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = listItemColors.containerColor
                                    )
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.arrow_back),
                                        stringResource(Res.string.back)
                                    )
                                }
                            },
                            colors = barColors,
                            scrollBehavior = scrollBehavior
                        )
                    },
                    containerColor = barColors.containerColor,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) { innerPadding ->
                    val insets = mergePaddingValues(innerPadding, contentPadding)
                    HelloRuanGreetingPanel(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(insets)
                            .padding(horizontal = 24.dp)
                    )
                }

                HelloRuanPhase.Chat -> HelloRuanSiQiChatWithDrawerTomato(
                    contentPadding = contentPadding,
                    onBack = onBack,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelloRuanSiQiChatWithDrawerTomato(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
) {
    val preferenceRepository: PreferenceRepository = koinInject()
    val barColors = topBarColors
    var serverBaseUrl by remember { mutableStateOf(JETCHAT_DEFAULT_SERVER_URL) }
    val authGateway: JetchatAuthGatewayTomato = rememberJetchatAuthGatewayTomato(serverBaseUrl)
    val serverGateway = rememberJetchatServerGatewayTomato(serverBaseUrl)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val contacts = remember { mutableStateListOf<JetchatContactTomato>() }
    val conversationStates = remember { mutableStateMapOf<String, JetchatConversationUiState>() }
    val contactConversationIdMap = remember { mutableStateMapOf<String, Long>() }
    val conversationCursorMap = remember { mutableStateMapOf<Long, Long>() }
    var authLoaded by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var authToken by remember { mutableStateOf("") }
    var currentUserId by remember { mutableStateOf(0L) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var selectedContactId by remember { mutableStateOf("") }
    var selectedProfileId by remember { mutableStateOf<String?>(null) }
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showServerConfigDialog by remember { mutableStateOf(false) }
    var newFriendName by remember { mutableStateOf("") }
    var serverUrlInput by remember { mutableStateOf(JETCHAT_DEFAULT_SERVER_URL) }
    var widgetPinMessage by remember { mutableStateOf<String?>(null) }
    var myAvatarMediaUrl by remember { mutableStateOf<String?>(null) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    val widgetPinRequestedText = stringResource(Res.string.jetchat_widget_pin_requested)
    val widgetPinFailedText = stringResource(Res.string.jetchat_widget_pin_failed)
    val loginErrorText = stringResource(Res.string.jetchat_login_error_empty)
    val chatInboundDiscreetNotifier = rememberChatInboundDiscreetNotifier()

    LaunchedEffect(preferenceRepository) {
        val savedLogin = preferenceRepository.getBooleanPreference(JETCHAT_LOGIN_STATE_KEY) ?: false
        val savedUser = preferenceRepository.getStringPreference(JETCHAT_LOGIN_USER_KEY).orEmpty()
        val savedToken = preferenceRepository.getStringPreference(JETCHAT_LOGIN_TOKEN_KEY).orEmpty()
        val savedUserId = preferenceRepository.getIntPreference(JETCHAT_LOGIN_USER_ID_KEY)?.toLong() ?: 0L
        val savedServerUrl = preferenceRepository.getStringPreference(JETCHAT_SERVER_BASE_URL_KEY)
            .orEmpty()
        isLoggedIn = savedLogin && savedUser.isNotBlank() && savedToken.isNotBlank() && savedUserId > 0L
        usernameInput = savedUser
        authToken = savedToken
        currentUserId = savedUserId
        serverBaseUrl = if (savedServerUrl.isBlank()) JETCHAT_DEFAULT_SERVER_URL else savedServerUrl
        serverUrlInput = serverBaseUrl
        authLoaded = true
    }

    fun resolveAuthMediaPath(path: String?): String? {
        val p = path?.trim().orEmpty().ifBlank { return null }
        val full = if (p.startsWith("http://") || p.startsWith("https://")) {
            p
        } else {
            "${serverBaseUrl.trimEnd('/')}$p"
        }
        return "$full#bearer=$authToken"
    }

    suspend fun refreshContactsAndConversations() {
        if (authToken.isBlank()) return
        runCatching {
            serverGateway.getMyProfile(authToken).onSuccess { p ->
                myAvatarMediaUrl = p.avatarMediaUrl.takeIf { it.isNotBlank() }
            }
        }
        val serverContacts = serverGateway.listFriends(authToken)
        contacts.clear()
        contacts.addAll(serverContacts)
        val conversations = serverGateway.listConversations(authToken)
        contactConversationIdMap.clear()
        conversations.forEach { conv ->
            contactConversationIdMap[conv.peerUserId.toString()] = conv.id
            conversationCursorMap.putIfAbsent(conv.id, 0L)
        }
        if (selectedContactId.isBlank() && contacts.isNotEmpty()) {
            selectedContactId = contacts.first().id
        }
        contacts.forEach { contact ->
            conversationStates.getOrPut(contact.id) {
                JetchatConversationUiState(
                    channelName = contact.displayName,
                    channelMembers = 2,
                    initialMessages = emptyList(),
                )
            }
        }
    }

    fun toUiMessage(contactId: String, row: org.nsh07.pomodoro.ui.settingsScreen.jetchat.JetchatServerMessageTomato): JetchatMessage {
        val author = if (row.senderId == currentUserId) "me" else {
            contacts.firstOrNull { it.id == contactId }?.displayName ?: "Friend"
        }
        val resolvedMediaUri = row.mediaUrl.takeIf { it.isNotBlank() }?.let { mediaPath ->
            if (mediaPath.startsWith("http://") || mediaPath.startsWith("https://")) {
                "$mediaPath#bearer=$authToken"
            } else {
                "${serverBaseUrl.trimEnd('/')}$mediaPath#bearer=$authToken"
            }
        }
        val profileNavId = if (row.senderId == currentUserId) JETCHAT_AUTHOR_ME else contactId
        val authorAvatarUrl = if (row.senderId == currentUserId) {
            resolveAuthMediaPath(myAvatarMediaUrl)
        } else {
            resolveAuthMediaPath(contacts.firstOrNull { it.id == contactId }?.avatarMediaUrl)
        }
        return JetchatMessage(
            author = author,
            content = row.contentText,
            timestamp = row.createdAt,
            localImageUri = resolvedMediaUri,
            serverMessageId = row.id,
            senderId = row.senderId,
            profileNavId = profileNavId,
            authorAvatarUrl = authorAvatarUrl,
        )
    }

    LaunchedEffect(isLoggedIn, authToken) {
        if (isLoggedIn && authToken.isNotBlank()) {
            runCatching { refreshContactsAndConversations() }
                .onFailure { loginError = it.message ?: "Failed to load conversations" }
        }
    }

    LaunchedEffect(isLoggedIn, authToken, selectedContactId, currentUserId, serverBaseUrl) {
        if (!isLoggedIn || authToken.isBlank() || selectedContactId.isBlank()) return@LaunchedEffect
        val conversationId = contactConversationIdMap[selectedContactId] ?: return@LaunchedEffect
        if (conversationCursorMap[conversationId] == null || conversationCursorMap[conversationId] == 0L) {
            runCatching {
                val initialRows = serverGateway.syncMessages(authToken, conversationId, afterId = 0L, limit = 200)
                val sorted = initialRows.sortedByDescending { it.id }
                conversationStates[selectedContactId]?.replaceAllMessages(sorted.map { toUiMessage(selectedContactId, it) })
                conversationCursorMap[conversationId] = initialRows.maxOfOrNull { it.id } ?: 0L
            }
        }
        while (true) {
            runCatching {
                val afterId = conversationCursorMap[conversationId] ?: 0L
                val deltaRows = serverGateway.syncMessages(authToken, conversationId, afterId = afterId, limit = 100)
                if (deltaRows.isNotEmpty()) {
                    val state = conversationStates[selectedContactId] ?: return@runCatching
                    val merged = (deltaRows.map { toUiMessage(selectedContactId, it) } + state.messages)
                        .distinctBy { it.serverMessageId ?: "${it.author}-${it.timestamp}-${it.content}" }
                        .sortedByDescending { it.serverMessageId ?: Long.MIN_VALUE }
                    state.replaceAllMessages(merged)
                    conversationCursorMap[conversationId] = deltaRows.maxOf { it.id }
                    if (deltaRows.any { it.senderId != currentUserId }) {
                        chatInboundDiscreetNotifier.notifyInboundFromPeer()
                    }
                }
            }
            delay(1500)
        }
    }

    widgetPinMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { widgetPinMessage = null },
            title = { Text(stringResource(Res.string.jetchat_not_available)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { widgetPinMessage = null }) {
                    Text(stringResource(Res.string.ok))
                }
            },
        )
    }
    if (showAddFriendDialog) {
        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = { Text(stringResource(Res.string.jetchat_drawer_add_friend)) },
            text = {
                OutlinedTextField(
                    value = newFriendName,
                    onValueChange = { newFriendName = it },
                    singleLine = true,
                    label = { Text("Name") },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val normalized = newFriendName.trim()
                        if (normalized.isNotEmpty()) {
                            scope.launch {
                                val added = serverGateway.addFriend(authToken, normalized)
                                if (added.isSuccess) {
                                    refreshContactsAndConversations()
                                    val created = added.getOrNull()
                                    if (created != null) {
                                        selectedContactId = created.id
                                    }
                                } else {
                                    loginError = added.exceptionOrNull()?.message ?: "Add friend failed"
                                }
                                newFriendName = ""
                                showAddFriendDialog = false
                                drawerState.close()
                            }
                        }
                    },
                ) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text(stringResource(Res.string.jetchat_clear_chat_history)) },
            text = { Text(stringResource(Res.string.jetchat_clear_chat_history_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val conversationId = contactConversationIdMap[selectedContactId]
                            if (conversationId != null && authToken.isNotBlank()) {
                                serverGateway.clearConversationMessages(authToken, conversationId)
                                    .onSuccess {
                                        conversationStates[selectedContactId]?.replaceAllMessages(emptyList())
                                        conversationCursorMap[conversationId] = 0L
                                    }
                                    .onFailure {
                                        loginError = it.message ?: "Clear history failed"
                                    }
                            }
                            showClearHistoryDialog = false
                        }
                    },
                ) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }
    if (showServerConfigDialog) {
        AlertDialog(
            onDismissRequest = { showServerConfigDialog = false },
            title = { Text("Server Connection") },
            text = {
                OutlinedTextField(
                    value = serverUrlInput,
                    onValueChange = { serverUrlInput = it },
                    singleLine = true,
                    label = { Text("Base URL") },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val normalized = serverUrlInput.trim().ifBlank { JETCHAT_DEFAULT_SERVER_URL }
                            serverBaseUrl = normalized
                            preferenceRepository.saveStringPreference(JETCHAT_SERVER_BASE_URL_KEY, normalized)
                            showServerConfigDialog = false
                            contactConversationIdMap.clear()
                            conversationCursorMap.clear()
                            refreshContactsAndConversations()
                        }
                    },
                ) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showServerConfigDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }

    ComposeBackHandler(enabled = true) {
        if (drawerState.currentValue == DrawerValue.Open) {
            scope.launch { drawerState.close() }
        } else {
            onBack()
        }
    }

    if (!authLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(Res.string.jetchat_login_required))
        }
        return
    }

    if (!isLoggedIn) {
        JetchatLoginPanelTomato(
            contentPadding = contentPadding,
            username = usernameInput,
            password = passwordInput,
            serverBaseUrl = serverUrlInput,
            loginError = loginError,
            onUsernameChanged = {
                usernameInput = it
                loginError = null
            },
            onPasswordChanged = {
                passwordInput = it
                loginError = null
            },
            onServerBaseUrlChanged = {
                serverUrlInput = it
                serverBaseUrl = it.trim().ifBlank { JETCHAT_DEFAULT_SERVER_URL }
                loginError = null
            },
            onLoginClicked = {
                scope.launch {
                    val normalizedUrl = serverUrlInput.trim().ifBlank { JETCHAT_DEFAULT_SERVER_URL }
                    serverBaseUrl = normalizedUrl
                    preferenceRepository.saveStringPreference(JETCHAT_SERVER_BASE_URL_KEY, normalizedUrl)
                    val result = authGateway.login(usernameInput, passwordInput)
                    if (result.success) {
                        val token = result.token.orEmpty()
                        val userId = result.userId ?: 0L
                        preferenceRepository.saveBooleanPreference(JETCHAT_LOGIN_STATE_KEY, true)
                        preferenceRepository.saveStringPreference(
                            JETCHAT_LOGIN_USER_KEY,
                            usernameInput.trim(),
                        )
                        preferenceRepository.saveStringPreference(JETCHAT_LOGIN_TOKEN_KEY, token)
                        preferenceRepository.saveIntPreference(JETCHAT_LOGIN_USER_ID_KEY, userId.toInt())
                        authToken = token
                        currentUserId = userId
                        isLoggedIn = true
                        loginError = null
                        myAvatarMediaUrl = result.avatarMediaUrl?.takeIf { it.isNotBlank() }
                        refreshContactsAndConversations()
                    } else {
                        loginError = result.errorMessage ?: loginErrorText
                    }
                }
            },
        )
        return
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                JetchatDrawerTomato(
                    selectedMenu = selectedContactId,
                    onChatClicked = {
                        selectedContactId = it
                        scope.launch { drawerState.close() }
                    },
                    onContactClicked = {
                        selectedContactId = it
                        scope.launch { drawerState.close() }
                    },
                    contacts = contacts,
                    mediaBaseUrl = serverBaseUrl,
                    authToken = authToken,
                    onAddFriendClicked = {
                        showAddFriendDialog = true
                    },
                    onServerConfigClicked = {
                        showServerConfigDialog = true
                    },
                    onLogoutClicked = {
                        scope.launch {
                            preferenceRepository.saveBooleanPreference(JETCHAT_LOGIN_STATE_KEY, false)
                            preferenceRepository.saveStringPreference(JETCHAT_LOGIN_TOKEN_KEY, "")
                            preferenceRepository.saveIntPreference(JETCHAT_LOGIN_USER_ID_KEY, 0)
                            authToken = ""
                            currentUserId = 0L
                            isLoggedIn = false
                            contacts.clear()
                            conversationStates.clear()
                            contactConversationIdMap.clear()
                            conversationCursorMap.clear()
                            selectedContactId = ""
                            drawerState.close()
                        }
                    },
                    onAddWidgetResult = { requested ->
                        widgetPinMessage = if (requested) {
                            widgetPinRequestedText
                        } else {
                            widgetPinFailedText
                        }
                        scope.launch { drawerState.close() }
                    },
                )
            }
        },
    ) {
        if (selectedProfileId == null) {
            if (contacts.isEmpty() || selectedContactId.isBlank()) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    containerColor = barColors.containerColor,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(stringResource(Res.string.jetchat_empty_contacts_title))
                            },
                            navigationIcon = {
                                JetchatNavIconTomato(
                                    contentDescription = stringResource(Res.string.jetchat_navigation_drawer_open),
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clickable { scope.launch { drawerState.open() } }
                                        .padding(16.dp),
                                )
                            },
                            colors = barColors,
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.jetchat_empty_contacts_message),
                            style = typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        if (!loginError.isNullOrBlank()) {
                            Text(
                                text = loginError.orEmpty(),
                                style = typography.bodySmall,
                                color = colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }
                        Button(
                            onClick = { showAddFriendDialog = true },
                            modifier = Modifier.padding(top = 20.dp),
                        ) {
                            Text(stringResource(Res.string.jetchat_drawer_add_friend))
                        }
                    }
                }
            } else {
                HelloRuanSiQiJetChatContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    onNavIconPressed = { scope.launch { drawerState.open() } },
                    navigateToProfile = { selectedProfileId = it },
                    onClearChatHistory = { showClearHistoryDialog = true },
                    uiState = conversationStates.getOrPut(selectedContactId) {
                        JetchatConversationUiState(
                            channelName = contacts.firstOrNull { it.id == selectedContactId }?.displayName ?: "Chat",
                            channelMembers = 2,
                            initialMessages = emptyList(),
                        )
                    },
                    currentUserId = currentUserId,
                    onSendTextMessage = { text ->
                        val conversationId = contactConversationIdMap[selectedContactId] ?: return@HelloRuanSiQiJetChatContent
                        scope.launch {
                            serverGateway.sendTextMessage(
                                token = authToken,
                                conversationId = conversationId,
                                content = text,
                                clientMsgId = "android-${kotlin.random.Random.nextLong()}",
                            )
                        }
                    },
                    onSendImageMessage = { localUri ->
                        val conversationId = contactConversationIdMap[selectedContactId] ?: return@HelloRuanSiQiJetChatContent
                        scope.launch {
                            val media = serverGateway.uploadMedia(authToken, localUri)
                            media.onSuccess { mediaUrl ->
                                serverGateway.sendMediaMessage(
                                    token = authToken,
                                    conversationId = conversationId,
                                    msgType = "image",
                                    mediaUrl = mediaUrl,
                                    clientMsgId = "android-img-${kotlin.random.Random.nextLong()}",
                                )
                            }.onFailure {
                                loginError = it.message ?: "Image upload failed"
                            }
                        }
                    },
                    onSendStickerMessage = { localUri ->
                        val conversationId = contactConversationIdMap[selectedContactId] ?: return@HelloRuanSiQiJetChatContent
                        scope.launch {
                            val media = serverGateway.uploadMedia(authToken, localUri)
                            media.onSuccess { mediaUrl ->
                                serverGateway.sendMediaMessage(
                                    token = authToken,
                                    conversationId = conversationId,
                                    msgType = "sticker",
                                    mediaUrl = mediaUrl,
                                    clientMsgId = "android-sticker-${kotlin.random.Random.nextLong()}",
                                )
                            }.onFailure {
                                loginError = it.message ?: "Sticker upload failed"
                            }
                        }
                    },
                )
            }
        } else {
            JetchatProfileScreenTomato(
                profileNavId = selectedProfileId.orEmpty(),
                currentUserId = currentUserId,
                serverBaseUrl = serverBaseUrl,
                authToken = authToken,
                gateway = serverGateway,
                contentPadding = contentPadding,
                onBackPressed = { selectedProfileId = null },
                onProfileUpdated = {
                    scope.launch { refreshContactsAndConversations() }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun JetchatLoginPanelTomato(
    contentPadding: PaddingValues,
    username: String,
    password: String,
    serverBaseUrl: String,
    loginError: String?,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onServerBaseUrlChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 24.dp),
    ) {
        Text(
            text = stringResource(Res.string.jetchat_login_title),
            style = typography.headlineMedium,
            modifier = Modifier.padding(top = 56.dp),
        )
        Text(
            text = stringResource(Res.string.jetchat_login_subtitle),
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChanged,
            singleLine = true,
            label = { Text(stringResource(Res.string.jetchat_login_hint)) },
            modifier = Modifier.widthIn(max = 520.dp),
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            singleLine = true,
            label = { Text("Password") },
            modifier = Modifier
                .widthIn(max = 520.dp)
                .padding(top = 12.dp),
        )
        OutlinedTextField(
            value = serverBaseUrl,
            onValueChange = onServerBaseUrlChanged,
            singleLine = true,
            label = { Text("Server URL") },
            modifier = Modifier
                .widthIn(max = 520.dp)
                .padding(top = 12.dp),
        )
        if (!loginError.isNullOrBlank()) {
            Text(
                text = loginError,
                style = typography.bodySmall,
                color = colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Button(
            onClick = onLoginClicked,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(stringResource(Res.string.jetchat_login_button))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HelloRuanGreetingPanel(
    modifier: Modifier = Modifier
) {
    val motionScheme = motionScheme
    var showGreeting by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showGreeting = true }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = showGreeting,
            enter = fadeIn(motionScheme.slowEffectsSpec()) +
                scaleIn(motionScheme.slowSpatialSpec(), initialScale = 0.85f) +
                expandVertically(motionScheme.slowSpatialSpec()),
            exit = fadeOut(motionScheme.defaultEffectsSpec()) +
                shrinkVertically(motionScheme.defaultSpatialSpec())
        ) {
            Text(
                text = GREETING,
                style = typography.displayMedium,
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontFamily = LocalAppFonts.current.topBarTitle
            )
        }
    }
}
