package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import tomato.shared.generated.resources.Res
import tomato.shared.generated.resources.jetchat_avatar_colleague
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val DEFAULT_SERVER_BASE_URL = "https://114.55.6.122:8443"

@Composable
actual fun rememberJetchatAuthGatewayTomato(baseUrl: String): JetchatAuthGatewayTomato = remember(baseUrl) {
    HttpJetchatAuthGatewayTomato(baseUrl.ifBlank { DEFAULT_SERVER_BASE_URL })
}

@Composable
actual fun rememberJetchatServerGatewayTomato(baseUrl: String): JetchatServerGatewayTomato {
    val context = LocalContext.current
    return remember(baseUrl) {
        HttpJetchatServerGatewayTomato(
            baseUrl = baseUrl.ifBlank { DEFAULT_SERVER_BASE_URL },
            fileBytesProvider = { uri -> readBytesFromUri(context, uri) },
            fileNameProvider = { uri -> guessFileName(uri) },
        )
    }
}

private class HttpJetchatAuthGatewayTomato(
    private val baseUrl: String,
) : JetchatAuthGatewayTomato {
    override suspend fun login(username: String, password: String): JetchatAuthResultTomato =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = JSONObject()
                    .put("username", username.trim())
                    .put("password", password)
                val response = requestJson(baseUrl, "/api/v1/auth/login", "POST", null, body.toString())
                val token = response.getString("token")
                val user = response.getJSONObject("user")
                JetchatAuthResultTomato(
                    success = true,
                    token = token,
                    userId = user.optLong("id"),
                    username = user.optString("username"),
                    bio = user.optString("bio").takeIf { it.isNotEmpty() },
                    avatarMediaUrl = user.optString("avatarMediaUrl").takeIf { it.isNotEmpty() },
                )
            }.getOrElse {
                JetchatAuthResultTomato(success = false, errorMessage = it.message ?: "Login failed")
            }
        }

}

private class HttpJetchatServerGatewayTomato(
    private val baseUrl: String,
    private val fileBytesProvider: (String) -> ByteArray?,
    private val fileNameProvider: (String) -> String,
) : JetchatServerGatewayTomato {
    override suspend fun listFriends(token: String): List<JetchatContactTomato> = withContext(Dispatchers.IO) {
        val response = requestJsonArray("/api/v1/friends", token)
        buildList {
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                val username = item.optString("username")
                if (username.isNotBlank()) {
                    add(
                        JetchatContactTomato(
                            id = item.optLong("id").toString(),
                            displayName = username,
                            avatar = Res.drawable.jetchat_avatar_colleague,
                            bio = item.optString("bio"),
                            avatarMediaUrl = item.optString("avatarMediaUrl").takeIf { it.isNotBlank() },
                        )
                    )
                }
            }
        }
    }

    override suspend fun addFriend(token: String, username: String): Result<JetchatContactTomato> =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = JSONObject().put("username", username.trim()).toString()
                val response = requestJson(baseUrl, "/api/v1/friends/add", "POST", token, body)
                val friend = response.getJSONObject("friend")
                JetchatContactTomato(
                    id = friend.optLong("id").toString(),
                    displayName = friend.optString("username"),
                    avatar = Res.drawable.jetchat_avatar_colleague,
                    bio = friend.optString("bio"),
                    avatarMediaUrl = friend.optString("avatarMediaUrl").takeIf { it.isNotBlank() },
                )
            }
        }

    override suspend fun listConversations(token: String): List<JetchatServerConversationTomato> =
        withContext(Dispatchers.IO) {
            val response = requestJsonArray("/api/v1/conversations", token)
            buildList {
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val peer = item.optJSONObject("peer") ?: continue
                    add(
                        JetchatServerConversationTomato(
                            id = item.optLong("id"),
                            peerUserId = peer.optLong("id"),
                            peerUsername = peer.optString("username"),
                            peerBio = peer.optString("bio"),
                            peerAvatarMediaUrl = peer.optString("avatarMediaUrl").takeIf { it.isNotBlank() },
                        )
                    )
                }
            }
        }

    override suspend fun syncMessages(
        token: String,
        conversationId: Long,
        afterId: Long,
        limit: Int,
    ): List<JetchatServerMessageTomato> = withContext(Dispatchers.IO) {
        val path = "/api/v1/messages/sync?conversationId=$conversationId&afterId=$afterId&limit=$limit"
        val response = requestJson(baseUrl, path, "GET", token, null)
        val rows = response.optJSONArray("messages") ?: JSONArray()
        buildList {
            for (i in 0 until rows.length()) {
                val item = rows.getJSONObject(i)
                add(
                    JetchatServerMessageTomato(
                        id = item.optLong("id"),
                        conversationId = item.optLong("conversationId"),
                        senderId = item.optLong("senderId"),
                        msgType = item.optString("msgType"),
                        contentText = item.optString("contentText"),
                        mediaUrl = item.optString("mediaUrl"),
                        createdAt = item.optString("createdAt"),
                    )
                )
            }
        }
    }

    override suspend fun sendTextMessage(
        token: String,
        conversationId: Long,
        content: String,
        clientMsgId: String,
    ): Result<JetchatServerMessageTomato> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject()
                .put("conversationId", conversationId)
                .put("msgType", "text")
                .put("contentText", content)
                .put("clientMsgId", clientMsgId)
                .toString()
            val item = requestJson(baseUrl, "/api/v1/messages/send", "POST", token, body)
            JetchatServerMessageTomato(
                id = item.optLong("id"),
                conversationId = item.optLong("conversationId"),
                senderId = item.optLong("senderId"),
                msgType = item.optString("msgType"),
                contentText = item.optString("contentText"),
                mediaUrl = item.optString("mediaUrl"),
                createdAt = item.optString("createdAt"),
            )
        }
    }

    override suspend fun uploadMedia(token: String, localUri: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val bytes = fileBytesProvider(localUri) ?: throw IllegalStateException("Cannot read local image")
                val fileName = fileNameProvider(localUri)
                val contentType = guessMimeType(fileName)
                val boundary = "----TomatoBoundary${UUID.randomUUID()}"
                val body = ByteArrayOutputStream().use { stream ->
                    stream.write("--$boundary\r\n".toByteArray())
                    stream.write(
                        "Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"\r\n"
                            .toByteArray()
                    )
                    stream.write("Content-Type: $contentType\r\n\r\n".toByteArray())
                    stream.write(bytes)
                    stream.write("\r\n--$boundary--\r\n".toByteArray())
                    stream.toByteArray()
                }

                val connection = createConnection("$baseUrl/api/v1/media/upload", "POST", token)
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                connection.doOutput = true
                DataOutputStream(connection.outputStream).use { out ->
                    out.write(body)
                }
                val response = readJsonResponse(connection)
                canonicalMediaUrlForApi(
                    response.optString("mediaUrl").ifBlank {
                        throw IllegalStateException("Server did not return mediaUrl")
                    },
                )
            }
        }

    override suspend fun sendMediaMessage(
        token: String,
        conversationId: Long,
        msgType: String,
        mediaUrl: String,
        clientMsgId: String,
    ): Result<JetchatServerMessageTomato> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject()
                .put("conversationId", conversationId)
                .put("msgType", msgType)
                .put("mediaUrl", mediaUrl)
                .put("clientMsgId", clientMsgId)
                .toString()
            val item = requestJson(baseUrl, "/api/v1/messages/send", "POST", token, body)
            JetchatServerMessageTomato(
                id = item.optLong("id"),
                conversationId = item.optLong("conversationId"),
                senderId = item.optLong("senderId"),
                msgType = item.optString("msgType"),
                contentText = item.optString("contentText"),
                mediaUrl = item.optString("mediaUrl"),
                createdAt = item.optString("createdAt"),
            )
        }
    }

    override suspend fun getMyProfile(token: String): Result<JetchatUserProfileTomato> =
        withContext(Dispatchers.IO) {
            runCatching {
                val obj = requestJson(baseUrl, "/api/v1/profile/me", "GET", token, null)
                parseUserProfileTomato(obj)
            }
        }

    override suspend fun getUserProfile(token: String, userId: Long): Result<JetchatUserProfileTomato> =
        withContext(Dispatchers.IO) {
            runCatching {
                val obj = requestJson(baseUrl, "/api/v1/users/$userId", "GET", token, null)
                parseUserProfileTomato(obj)
            }
        }

    override suspend fun updateMyProfile(
        token: String,
        bio: String?,
        avatarMediaUrl: String?,
    ): Result<JetchatUserProfileTomato> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject()
            if (bio != null) {
                body.put("bio", bio)
            }
            if (avatarMediaUrl != null) {
                body.put("avatarMediaUrl", canonicalMediaUrlForApi(avatarMediaUrl))
            }
            if (body.length() == 0) {
                throw IllegalStateException("Nothing to update")
            }
            val obj = requestJson(baseUrl, "/api/v1/profile/me", "PATCH", token, body.toString())
            parseUserProfileTomato(obj)
        }
    }

    override suspend fun clearConversationMessages(token: String, conversationId: Long): Result<Int> =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = JSONObject().put("conversationId", conversationId).toString()
                val obj = requestJson(baseUrl, "/api/v1/messages/clear", "POST", token, body)
                obj.optInt("clearedCount")
            }
        }

    private fun requestJsonArray(path: String, token: String): JSONArray {
        val connection = createConnection("$baseUrl$path", "GET", token)
        val text = readResponseText(connection)
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException("HTTP ${connection.responseCode}: $text")
        }
        return JSONArray(text)
    }
}

/**
 * Server stores and matches `media_assets.media_url` as a path like `/media/2026/04/....`.
 * Strip any `https?://...` prefix so the path matches the DB row.
 */
private fun canonicalMediaUrlForApi(raw: String): String {
    val s = raw.trim()
    if (s.isEmpty()) return s
    val marker = "/media/"
    val idx = s.indexOf(marker)
    if (idx >= 0) {
        return s.substring(idx)
    }
    return s
}

private fun requestJson(baseUrl: String, path: String, method: String, token: String?, body: String?): JSONObject {
    val connection = createConnection("$baseUrl$path", method, token)
    if (body != null) {
        connection.doOutput = true
        DataOutputStream(connection.outputStream).use { out ->
            out.write(body.toByteArray(Charsets.UTF_8))
        }
    }
    return readJsonResponse(connection)
}

private fun createConnection(rawUrl: String, method: String, token: String?): HttpURLConnection {
    installUnsafeSslForDev()
    val connection = URL(rawUrl).openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.connectTimeout = 20_000
    connection.readTimeout = 60_000
    connection.setRequestProperty("Accept", "application/json")
    connection.setRequestProperty("Content-Type", "application/json")
    if (!token.isNullOrBlank()) {
        connection.setRequestProperty("Authorization", "Bearer $token")
    }
    return connection
}

private fun readJsonResponse(connection: HttpURLConnection): JSONObject {
    val text = readResponseText(connection)
    if (connection.responseCode !in 200..299) {
        throw IllegalStateException("HTTP ${connection.responseCode}: $text")
    }
    return JSONObject(text)
}

private fun parseUserProfileTomato(obj: JSONObject): JetchatUserProfileTomato =
    JetchatUserProfileTomato(
        id = obj.optLong("id"),
        username = obj.optString("username"),
        bio = obj.optString("bio"),
        avatarMediaUrl = obj.optString("avatarMediaUrl"),
    )

private fun readResponseText(connection: HttpURLConnection): String {
    val input = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
    if (input == null) return ""
    return BufferedReader(InputStreamReader(BufferedInputStream(input))).use { it.readText() }
}

@Volatile
private var unsafeSslInstalled = false

private fun installUnsafeSslForDev() {
    if (unsafeSslInstalled) return
    val trustAllManagers = arrayOf<TrustManager>(
        object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = emptyArray()
        }
    )
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAllManagers, java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier(HostnameVerifier { _, _ -> true })
    unsafeSslInstalled = true
}

private fun readBytesFromUri(context: android.content.Context, uri: String): ByteArray? {
    return runCatching {
        context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use { stream ->
            stream.readBytes()
        }
    }.getOrNull()
}

private fun guessFileName(uri: String): String {
    val parsed = android.net.Uri.parse(uri)
    val tail = parsed.lastPathSegment?.substringAfterLast('/')?.substringAfterLast('\\').orEmpty()
    return if (tail.contains('.')) tail else "${UUID.randomUUID()}.png"
}

private fun guessMimeType(fileName: String): String {
    val lower = fileName.lowercase()
    return when {
        lower.endsWith(".png") -> "image/png"
        lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
        lower.endsWith(".webp") -> "image/webp"
        lower.endsWith(".gif") -> "image/gif"
        else -> "application/octet-stream"
    }
}

