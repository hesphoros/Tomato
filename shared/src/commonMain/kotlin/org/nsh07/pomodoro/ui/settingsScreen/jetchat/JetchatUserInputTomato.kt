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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.email
import tomato.shared.generated.resources.folder
import tomato.shared.generated.resources.globe
import tomato.shared.generated.resources.jetchat_attach_photo_desc
import tomato.shared.generated.resources.jetchat_dm_desc
import tomato.shared.generated.resources.jetchat_emoji_selector_bt_desc
import tomato.shared.generated.resources.jetchat_map_selector_desc
import tomato.shared.generated.resources.jetchat_not_available
import tomato.shared.generated.resources.jetchat_not_available_subtitle
import tomato.shared.generated.resources.jetchat_send
import tomato.shared.generated.resources.jetchat_textfield_desc
import tomato.shared.generated.resources.jetchat_textfield_hint
import tomato.shared.generated.resources.jetchat_videochat_desc
import tomato.shared.generated.resources.mobile_text
import tomato.shared.generated.resources.play_large

internal enum class InputSelectorTomato {
    NONE,
    MAP,
    DM,
    EMOJI,
    PHONE,
    PICTURE,
}

/**
 * JetChat [UserInput](https://github.com/android/compose-samples/blob/main/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/UserInput.kt)
 * (voice recording UI omitted; emoji + selector row + send button match the sample).
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun JetchatUserInputTomato(
    onMessageSent: (String) -> Unit,
    onStickerSent: (DrawableResource) -> Unit = {},
    onLocalStickerSent: (String) -> Unit = {},
    localStickerUris: List<String> = emptyList(),
    onLocalImageSent: (String) -> Unit = {},
    resetScroll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelectorTomato.NONE) }
    val dismissKeyboard = { currentInputSelector = InputSelectorTomato.NONE }

    if (currentInputSelector != InputSelectorTomato.NONE) {
        ComposeBackHandler(enabled = true, onBack = dismissKeyboard)
    }

    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var textFieldFocusState by remember { mutableStateOf(false) }
    val launchImagePicker = rememberJetchatImagePickerLauncher { uri ->
        onLocalImageSent(uri)
        dismissKeyboard()
        resetScroll()
    }
    val launchStickerPicker = rememberJetchatImagePickerLauncher { uri ->
        onLocalStickerSent(uri)
        dismissKeyboard()
        resetScroll()
    }

    Box(modifier = modifier.imePadding()) {
        Surface(
            tonalElevation = 2.dp,
            contentColor = colorScheme.secondary,
        ) {
            Column {
            JetchatUserInputTextTomato(
                textFieldValue = textState,
                onTextChanged = { textState = it },
                keyboardShown = currentInputSelector == InputSelectorTomato.NONE && textFieldFocusState,
                onTextFieldFocused = { focused ->
                    if (focused) {
                        currentInputSelector = InputSelectorTomato.NONE
                        resetScroll()
                    }
                    textFieldFocusState = focused
                },
                onMessageSent = {
                    onMessageSent(textState.text)
                    textState = TextFieldValue()
                    resetScroll()
                },
                focusState = textFieldFocusState,
            )
            JetchatUserInputSelectorTomato(
                onSelectorChange = { currentInputSelector = it },
                onPictureSelected = launchImagePicker,
                sendMessageEnabled = textState.text.isNotBlank(),
                onMessageSent = {
                    onMessageSent(textState.text)
                    textState = TextFieldValue()
                    resetScroll()
                    dismissKeyboard()
                },
                currentInputSelector = currentInputSelector,
            )
            JetchatSelectorExpandedTomato(
                currentSelector = currentInputSelector,
                onCloseRequested = dismissKeyboard,
                onTextAdded = { textState = textState.addTextTomato(it) },
                onStickerSelected = { sticker ->
                    onStickerSent(sticker)
                    dismissKeyboard()
                    resetScroll()
                },
                onLocalStickerSent = { uri ->
                    onLocalStickerSent(uri)
                    dismissKeyboard()
                    resetScroll()
                },
                onImportLocalSticker = launchStickerPicker,
                localStickerUris = localStickerUris,
            )
            }
        }
    }
}

private fun TextFieldValue.addTextTomato(newString: String): TextFieldValue {
    val newText = text.replaceRange(selection.start, selection.end, newString)
    val newSelection = androidx.compose.ui.text.TextRange(newText.length, newText.length)
    return copy(text = newText, selection = newSelection)
}

@Composable
private fun JetchatSelectorExpandedTomato(
    currentSelector: InputSelectorTomato,
    onCloseRequested: () -> Unit,
    onTextAdded: (String) -> Unit,
    onStickerSelected: (DrawableResource) -> Unit,
    onLocalStickerSent: (String) -> Unit,
    onImportLocalSticker: () -> Unit,
    localStickerUris: List<String>,
) {
    if (currentSelector == InputSelectorTomato.NONE) return

    val focusRequester = remember { FocusRequester() }
    SideEffect {
        if (currentSelector == InputSelectorTomato.EMOJI) {
            focusRequester.requestFocus()
        }
    }

    Surface(tonalElevation = 8.dp) {
        when (currentSelector) {
            InputSelectorTomato.EMOJI ->
                JetchatEmojiSelectorTomato(
                    onTextAdded = onTextAdded,
                    focusRequester = focusRequester,
                    onStickerSelected = onStickerSelected,
                    onLocalStickerSent = onLocalStickerSent,
                    onImportLocalSticker = onImportLocalSticker,
                    localStickerUris = localStickerUris,
                )

            InputSelectorTomato.DM -> JetchatFunctionalityNotAvailablePanelTomato()
            InputSelectorTomato.PICTURE,
            InputSelectorTomato.MAP,
            InputSelectorTomato.PHONE,
            -> JetchatFunctionalityNotAvailablePanelTomato()

            InputSelectorTomato.NONE -> Unit
        }
    }
}

@Composable
private fun JetchatFunctionalityNotAvailablePanelTomato() {
    AnimatedVisibility(
        visible = true,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .height(320.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.jetchat_not_available),
                style = typography.titleMedium,
            )
            Text(
                text = stringResource(Res.string.jetchat_not_available_subtitle),
                modifier = Modifier.padding(top = 32.dp),
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun JetchatUserInputSelectorTomato(
    onSelectorChange: (InputSelectorTomato) -> Unit,
    onPictureSelected: () -> Unit,
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit,
    currentInputSelector: InputSelectorTomato,
) {
    Row(
        modifier = Modifier
            .height(72.dp)
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InputSelectorButtonTomato(
            onClick = { onSelectorChange(InputSelectorTomato.EMOJI) },
            icon = painterResource(Res.drawable.mobile_text),
            selected = currentInputSelector == InputSelectorTomato.EMOJI,
            description = stringResource(Res.string.jetchat_emoji_selector_bt_desc),
        )
        InputSelectorButtonTomato(
            onClick = { onSelectorChange(InputSelectorTomato.DM) },
            icon = painterResource(Res.drawable.email),
            selected = currentInputSelector == InputSelectorTomato.DM,
            description = stringResource(Res.string.jetchat_dm_desc),
        )
        InputSelectorButtonTomato(
            onClick = onPictureSelected,
            icon = painterResource(Res.drawable.folder),
            selected = false,
            description = stringResource(Res.string.jetchat_attach_photo_desc),
        )
        InputSelectorButtonTomato(
            onClick = { onSelectorChange(InputSelectorTomato.MAP) },
            icon = painterResource(Res.drawable.globe),
            selected = currentInputSelector == InputSelectorTomato.MAP,
            description = stringResource(Res.string.jetchat_map_selector_desc),
        )
        InputSelectorButtonTomato(
            onClick = { onSelectorChange(InputSelectorTomato.PHONE) },
            icon = painterResource(Res.drawable.play_large),
            selected = currentInputSelector == InputSelectorTomato.PHONE,
            description = stringResource(Res.string.jetchat_videochat_desc),
        )

        val border = if (!sendMessageEnabled) {
            BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.3f))
        } else null
        Spacer(modifier = Modifier.weight(1f))
        val disabledContentColor = colorScheme.onSurface.copy(alpha = 0.3f)
        val buttonColors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Transparent,
            disabledContentColor = disabledContentColor,
        )
        Button(
            modifier = Modifier.height(36.dp),
            enabled = sendMessageEnabled,
            onClick = onMessageSent,
            colors = buttonColors,
            border = border,
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                stringResource(Res.string.jetchat_send),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun InputSelectorButtonTomato(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.painter.Painter,
    description: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundModifier = if (selected) {
        Modifier.background(
            color = LocalContentColor.current,
            shape = RoundedCornerShape(14.dp),
        )
    } else {
        Modifier
    }
    IconButton(
        onClick = onClick,
        modifier = modifier.then(backgroundModifier),
    ) {
        val tint = if (selected) {
            contentColorFor(backgroundColor = LocalContentColor.current)
        } else {
            LocalContentColor.current
        }
        Icon(
            icon,
            tint = tint,
            modifier = Modifier
                .padding(8.dp)
                .size(56.dp),
            contentDescription = description,
        )
    }
}

@Composable
private fun JetchatUserInputTextTomato(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    onMessageSent: (String) -> Unit,
    focusState: Boolean,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp, max = 140.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                },
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = onTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
                    .align(Alignment.CenterStart)
                    .focusRequester(focusRequester)
                    .onFocusChanged { onTextFieldFocused(it.isFocused) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default,
                ),
                keyboardActions = KeyboardActions(),
                maxLines = 5,
                cursorBrush = SolidColor(LocalContentColor.current),
                textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            )
            val disableContentColor = colorScheme.onSurfaceVariant
            if (textFieldValue.text.isEmpty() && !focusState) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 32.dp),
                    text = stringResource(Res.string.jetchat_textfield_hint),
                    style = typography.bodyLarge.copy(color = disableContentColor),
                )
            }
        }
        Spacer(Modifier.size(56.dp))
    }
}
