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

package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import android.content.ClipDescription
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent

actual fun Modifier.helloRuanConversationPlainTextDrop(
    onPlainTextDropped: (String) -> Boolean,
    lifecycle: HelloRuanConversationDropLifecycle,
): Modifier =
    this.dragAndDropTarget(
        shouldStartDragAndDrop = { event ->
            event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
        },
        target = object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val clipData = event.toAndroidDragEvent().clipData
                if (clipData.itemCount < 1) return false
                return onPlainTextDropped(clipData.getItemAt(0).text.toString())
            }

            override fun onStarted(event: DragAndDropEvent) {
                super.onStarted(event)
                lifecycle.onStarted()
            }

            override fun onEntered(event: DragAndDropEvent) {
                super.onEntered(event)
                lifecycle.onEntered()
            }

            override fun onExited(event: DragAndDropEvent) {
                super.onExited(event)
                lifecycle.onExited()
            }

            override fun onEnded(event: DragAndDropEvent) {
                super.onEnded(event)
                lifecycle.onEnded()
            }
        },
    )
