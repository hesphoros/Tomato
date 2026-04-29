package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.nsh07.pomodoro.shared.R
import java.net.HttpURLConnection
import java.net.URL

@Composable
actual fun JetchatPlatformImageTomato(
    uri: String,
    modifier: Modifier,
    contentDescription: String?,
) {
    val context = LocalContext.current
    val bitmap: ImageBitmap? by produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val parsedUri = Uri.parse(uri)
                if (uri.startsWith("http://") || uri.startsWith("https://")) {
                    val pureUrl = uri.substringBefore("#")
                    val bearerToken = parsedUri.fragment
                        ?.takeIf { it.startsWith("bearer=") }
                        ?.removePrefix("bearer=")
                    val connection = URL(pureUrl).openConnection() as HttpURLConnection
                    connection.connectTimeout = 6000
                    connection.readTimeout = 8000
                    if (!bearerToken.isNullOrBlank()) {
                        connection.setRequestProperty("Authorization", "Bearer $bearerToken")
                    }
                    connection.inputStream.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                } else {
                    context.contentResolver.openInputStream(parsedUri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }
            }.getOrNull()
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            modifier = modifier,
            contentDescription = contentDescription,
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            modifier = modifier,
            contentDescription = contentDescription,
        )
    }
}
