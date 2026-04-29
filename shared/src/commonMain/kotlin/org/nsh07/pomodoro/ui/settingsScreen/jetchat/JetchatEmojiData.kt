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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.folder
import tomato.shared.generated.resources.jetchat_emojis_label
import tomato.shared.generated.resources.jetchat_emoji_selector_desc
import tomato.shared.generated.resources.jetchat_import_local_sticker
import tomato.shared.generated.resources.jetchat_my_stickers_label
import tomato.shared.generated.resources.jetchat_sticker_pack_one
import tomato.shared.generated.resources.jetchat_sticker_pack_two
import tomato.shared.generated.resources.jetchat_stickers_label
import tomato.shared.generated.resources.wa_pack1_01
import tomato.shared.generated.resources.wa_pack1_02
import tomato.shared.generated.resources.wa_pack1_03
import tomato.shared.generated.resources.wa_pack1_04
import tomato.shared.generated.resources.wa_pack1_05
import tomato.shared.generated.resources.wa_pack1_06
import tomato.shared.generated.resources.wa_pack1_07
import tomato.shared.generated.resources.wa_pack1_08
import tomato.shared.generated.resources.wa_pack1_09
import tomato.shared.generated.resources.wa_pack1_10
import tomato.shared.generated.resources.wa_pack1_11
import tomato.shared.generated.resources.wa_pack1_12
import tomato.shared.generated.resources.wa_pack1_13
import tomato.shared.generated.resources.wa_pack1_14
import tomato.shared.generated.resources.wa_pack1_15
import tomato.shared.generated.resources.wa_pack1_16
import tomato.shared.generated.resources.wa_pack1_17
import tomato.shared.generated.resources.wa_pack1_18
import tomato.shared.generated.resources.wa_pack1_19
import tomato.shared.generated.resources.wa_pack1_20
import tomato.shared.generated.resources.wa_pack1_21
import tomato.shared.generated.resources.wa_pack1_22
import tomato.shared.generated.resources.wa_pack1_23
import tomato.shared.generated.resources.wa_pack1_24
import tomato.shared.generated.resources.wa_pack1_25
import tomato.shared.generated.resources.wa_pack2_01
import tomato.shared.generated.resources.wa_pack2_02
import tomato.shared.generated.resources.wa_pack2_03
import tomato.shared.generated.resources.wa_pack2_04
import tomato.shared.generated.resources.wa_pack2_05
import tomato.shared.generated.resources.wa_pack2_06
import tomato.shared.generated.resources.wa_pack2_07
import tomato.shared.generated.resources.wa_pack2_08
import tomato.shared.generated.resources.wa_pack2_09
import tomato.shared.generated.resources.wa_pack2_10
import tomato.shared.generated.resources.wa_pack2_11
import tomato.shared.generated.resources.wa_pack2_12
import tomato.shared.generated.resources.wa_pack2_13
import tomato.shared.generated.resources.wa_pack2_14
import tomato.shared.generated.resources.wa_pack2_15
import tomato.shared.generated.resources.wa_pack2_16
import tomato.shared.generated.resources.wa_pack2_17
import tomato.shared.generated.resources.wa_pack2_18
import tomato.shared.generated.resources.wa_pack2_19
import tomato.shared.generated.resources.wa_pack2_20
import tomato.shared.generated.resources.wa_pack2_21

private const val EMOJI_COLUMNS = 10

private val jetchatEmojiGrid: List<String> = listOf(
    "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE04", "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE09", "\uD83D\uDE0A", "\uD83D\uDE0B",
    "\uD83D\uDE0E", "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE17", "\uD83D\uDE1A", "\u263A", "\uD83D\uDE42", "\uD83E\uDD17", "\uD83D\uDE07", "\uD83E\uDD13",
    "\uD83E\uDD14", "\uD83D\uDE10", "\uD83D\uDE11", "\uD83D\uDE36", "\uD83D\uDE44", "\uD83D\uDE0F", "\uD83D\uDE23", "\uD83D\uDE25", "\uD83D\uDE2E", "\uD83E\uDD10",
    "\uD83D\uDE2F", "\uD83D\uDE2A", "\uD83D\uDE2B", "\uD83D\uDE34", "\uD83D\uDE0C", "\uD83D\uDE1B", "\uD83D\uDE1C", "\uD83D\uDE1D", "\uD83D\uDE12", "\uD83D\uDE13",
)

internal enum class EmojiStickerSelectorTomato { EMOJI, STICKER }
private enum class StickerPackTomato { PACK_ONE, PACK_TWO }

@Composable
internal fun JetchatEmojiSelectorTomato(
    onTextAdded: (String) -> Unit,
    focusRequester: FocusRequester,
    onStickerSelected: (DrawableResource) -> Unit,
    onLocalStickerSent: (String) -> Unit,
    onImportLocalSticker: () -> Unit,
    localStickerUris: List<String>,
) {
    var selected by remember { mutableStateOf(EmojiStickerSelectorTomato.EMOJI) }
    var selectedPack by remember { mutableStateOf(StickerPackTomato.PACK_ONE) }
    val a11yLabel = stringResource(Res.string.jetchat_emoji_selector_desc)
    Column(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusTarget()
            .semantics { contentDescription = a11yLabel },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            ExtendedSelectorInnerButtonTomato(
                text = stringResource(Res.string.jetchat_emojis_label),
                onClick = { selected = EmojiStickerSelectorTomato.EMOJI },
                selected = selected == EmojiStickerSelectorTomato.EMOJI,
                modifier = Modifier.weight(1f),
            )
            ExtendedSelectorInnerButtonTomato(
                text = stringResource(Res.string.jetchat_stickers_label),
                onClick = {
                    selected = EmojiStickerSelectorTomato.STICKER
                },
                selected = selected == EmojiStickerSelectorTomato.STICKER,
                modifier = Modifier.weight(1f),
            )
        }
        Row(modifier = Modifier.verticalScroll(rememberScrollState())) {
            if (selected == EmojiStickerSelectorTomato.EMOJI) {
                JetchatEmojiTableTomato(onTextAdded, Modifier.padding(8.dp))
            } else {
                Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ExtendedSelectorInnerButtonTomato(
                            text = stringResource(Res.string.jetchat_sticker_pack_one),
                            onClick = { selectedPack = StickerPackTomato.PACK_ONE },
                            selected = selectedPack == StickerPackTomato.PACK_ONE,
                            modifier = Modifier.weight(1f),
                        )
                        ExtendedSelectorInnerButtonTomato(
                            text = stringResource(Res.string.jetchat_sticker_pack_two),
                            onClick = { selectedPack = StickerPackTomato.PACK_TWO },
                            selected = selectedPack == StickerPackTomato.PACK_TWO,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    JetchatStickerTableTomato(
                        stickers = if (selectedPack == StickerPackTomato.PACK_ONE) {
                            jetchatStickerPackOne
                        } else {
                            jetchatStickerPackTwo
                        },
                        onStickerSelected = onStickerSelected,
                        onLocalStickerSent = onLocalStickerSent,
                        onImportLocalSticker = onImportLocalSticker,
                        localStickerUris = localStickerUris,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExtendedSelectorInnerButtonTomato(
    text: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = ButtonDefaults.buttonColors(
        containerColor = if (selected) colorScheme.onSurface.copy(alpha = 0.08f) else Color.Transparent,
        disabledContainerColor = Color.Transparent,
        contentColor = colorScheme.onSurface,
        disabledContentColor = colorScheme.onSurface.copy(alpha = 0.74f),
    )
    TextButton(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .height(36.dp),
        colors = colors,
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(text = text, style = typography.titleSmall)
    }
}

@Composable
private fun JetchatEmojiTableTomato(onTextAdded: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        val rows = jetchatEmojiGrid.size / EMOJI_COLUMNS
        repeat(rows) { x ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(EMOJI_COLUMNS) { y ->
                    val idx = x * EMOJI_COLUMNS + y
                    if (idx < jetchatEmojiGrid.size) {
                        val emoji = jetchatEmojiGrid[idx]
                        Text(
                            modifier = Modifier
                                .clickable(onClick = { onTextAdded(emoji) })
                                .sizeIn(minWidth = 42.dp, minHeight = 42.dp)
                                .padding(8.dp),
                            text = emoji,
                            style = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                }
            }
        }
    }
}

private val jetchatStickerPackOne: List<DrawableResource> = listOf(
    Res.drawable.wa_pack1_01, Res.drawable.wa_pack1_02, Res.drawable.wa_pack1_03,
    Res.drawable.wa_pack1_04, Res.drawable.wa_pack1_05, Res.drawable.wa_pack1_06,
    Res.drawable.wa_pack1_07, Res.drawable.wa_pack1_08, Res.drawable.wa_pack1_09,
    Res.drawable.wa_pack1_10, Res.drawable.wa_pack1_11, Res.drawable.wa_pack1_12,
    Res.drawable.wa_pack1_13, Res.drawable.wa_pack1_14, Res.drawable.wa_pack1_15,
    Res.drawable.wa_pack1_16, Res.drawable.wa_pack1_17, Res.drawable.wa_pack1_18,
    Res.drawable.wa_pack1_19, Res.drawable.wa_pack1_20, Res.drawable.wa_pack1_21,
    Res.drawable.wa_pack1_22, Res.drawable.wa_pack1_23, Res.drawable.wa_pack1_24,
    Res.drawable.wa_pack1_25,
)

private val jetchatStickerPackTwo: List<DrawableResource> = listOf(
    Res.drawable.wa_pack2_01, Res.drawable.wa_pack2_02, Res.drawable.wa_pack2_03,
    Res.drawable.wa_pack2_04, Res.drawable.wa_pack2_05, Res.drawable.wa_pack2_06,
    Res.drawable.wa_pack2_07, Res.drawable.wa_pack2_08, Res.drawable.wa_pack2_09,
    Res.drawable.wa_pack2_10, Res.drawable.wa_pack2_11, Res.drawable.wa_pack2_12,
    Res.drawable.wa_pack2_13, Res.drawable.wa_pack2_14, Res.drawable.wa_pack2_15,
    Res.drawable.wa_pack2_16, Res.drawable.wa_pack2_17, Res.drawable.wa_pack2_18,
    Res.drawable.wa_pack2_19, Res.drawable.wa_pack2_20, Res.drawable.wa_pack2_21,
)

@Composable
private fun JetchatStickerTableTomato(
    stickers: List<DrawableResource>,
    onStickerSelected: (DrawableResource) -> Unit,
    onLocalStickerSent: (String) -> Unit,
    onImportLocalSticker: () -> Unit,
    localStickerUris: List<String>,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TextButton(
                onClick = onImportLocalSticker,
                modifier = Modifier.padding(8.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.folder),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(Res.string.jetchat_import_local_sticker))
            }
        }
        if (localStickerUris.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.jetchat_my_stickers_label),
                style = typography.bodyMedium,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 8.dp),
            )
            LocalStickerTableTomato(
                localStickerUris = localStickerUris,
                onLocalStickerSent = onLocalStickerSent,
            )
        }
        val columns = 4
        val rows = (stickers.size + columns - 1) / columns
        repeat(rows) { x ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(columns) { y ->
                    val idx = x * columns + y
                    if (idx < stickers.size) {
                        val sticker = stickers[idx]
                        Box(
                            modifier = Modifier
                                .size(86.dp)
                                .clickable { onStickerSelected(sticker) }
                                .padding(6.dp),
                        ) {
                            Image(
                                painter = painterResource(sticker),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    } else {
                        Spacer(Modifier.size(86.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LocalStickerTableTomato(
    localStickerUris: List<String>,
    onLocalStickerSent: (String) -> Unit,
) {
    val columns = 4
    val rows = (localStickerUris.size + columns - 1) / columns
    repeat(rows) { x ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(columns) { y ->
                val idx = x * columns + y
                if (idx < localStickerUris.size) {
                    val uri = localStickerUris[idx]
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clickable { onLocalStickerSent(uri) }
                            .padding(6.dp),
                    ) {
                        JetchatPlatformImageTomato(
                            uri = uri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    Spacer(Modifier.size(86.dp))
                }
            }
        }
    }
}
