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

import android.annotation.SuppressLint
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.nsh07.pomodoro.shared.R

private const val CHAT_DISCREET_NOTIFICATION_ID = 91043
private const val NOTIFY_THROTTLE_MS = 5_000L

private class AndroidChatInboundDiscreetNotifier(
    private val appContext: Context,
) : ChatInboundDiscreetNotifier {
    private var lastNotifyAtMs: Long = 0L

    @SuppressLint("MissingPermission")
    override fun notifyInboundFromPeer() {
        val now = System.currentTimeMillis()
        if (now - lastNotifyAtMs < NOTIFY_THROTTLE_MS) {
            return
        }
        lastNotifyAtMs = now

        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) {
                return
            }
        }

        val nm = NotificationManagerCompat.from(appContext)
        if (!nm.areNotificationsEnabled()) {
            return
        }

        val launch = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
        val contentIntent = PendingIntent.getActivity(
            appContext,
            CHAT_DISCREET_NOTIFICATION_ID,
            launch,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = appContext.getString(R.string.jetchat_discreet_notif_title)
        val text = appContext.getString(R.string.jetchat_discreet_notif_text)
        val smallIcon = appContext.applicationInfo.icon

        val notification = NotificationCompat.Builder(appContext, "timer")
            .setSmallIcon(smallIcon)
            .setColor(Color.RED)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setShowWhen(true)
            .setSilent(true)
            .setAutoCancel(true)
            .setOngoing(false)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .build()

        nm.notify(CHAT_DISCREET_NOTIFICATION_ID, notification)
    }
}

@Composable
actual fun rememberChatInboundDiscreetNotifier(): ChatInboundDiscreetNotifier {
    val context = LocalContext.current
    return remember {
        AndroidChatInboundDiscreetNotifier(context.applicationContext)
    }
}
