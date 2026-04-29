package org.nsh07.pomodoro.ui.settingsScreen.jetchat

import androidx.compose.runtime.Composable

data class JetchatAuthResultTomato(
    val success: Boolean,
    val token: String? = null,
    val userId: Long? = null,
    val username: String? = null,
    val bio: String? = null,
    val avatarMediaUrl: String? = null,
    val errorMessage: String? = null,
)

data class JetchatUserProfileTomato(
    val id: Long,
    val username: String,
    val bio: String,
    val avatarMediaUrl: String,
)

interface JetchatAuthGatewayTomato {
    suspend fun login(username: String, password: String): JetchatAuthResultTomato
}

class LocalBypassAuthGatewayTomato : JetchatAuthGatewayTomato {
    override suspend fun login(username: String, password: String): JetchatAuthResultTomato {
        return if (username.trim().isNotEmpty() && password.trim().isNotEmpty()) {
            JetchatAuthResultTomato(success = true, username = username.trim())
        } else {
            JetchatAuthResultTomato(success = false, errorMessage = "Username/password cannot be empty")
        }
    }
}

@Composable
expect fun rememberJetchatAuthGatewayTomato(baseUrl: String): JetchatAuthGatewayTomato

data class JetchatServerConversationTomato(
    val id: Long,
    val peerUserId: Long,
    val peerUsername: String,
    val peerBio: String = "",
    val peerAvatarMediaUrl: String? = null,
)

data class JetchatServerMessageTomato(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val msgType: String,
    val contentText: String,
    val mediaUrl: String,
    val createdAt: String,
)

interface JetchatServerGatewayTomato {
    suspend fun listFriends(token: String): List<JetchatContactTomato>
    suspend fun addFriend(token: String, username: String): Result<JetchatContactTomato>
    suspend fun listConversations(token: String): List<JetchatServerConversationTomato>
    suspend fun syncMessages(
        token: String,
        conversationId: Long,
        afterId: Long,
        limit: Int = 100,
    ): List<JetchatServerMessageTomato>
    suspend fun sendTextMessage(
        token: String,
        conversationId: Long,
        content: String,
        clientMsgId: String,
    ): Result<JetchatServerMessageTomato>
    suspend fun uploadMedia(
        token: String,
        localUri: String,
    ): Result<String>
    suspend fun sendMediaMessage(
        token: String,
        conversationId: Long,
        msgType: String,
        mediaUrl: String,
        clientMsgId: String,
    ): Result<JetchatServerMessageTomato>
    suspend fun getMyProfile(token: String): Result<JetchatUserProfileTomato>
    suspend fun getUserProfile(token: String, userId: Long): Result<JetchatUserProfileTomato>
    suspend fun updateMyProfile(
        token: String,
        bio: String?,
        avatarMediaUrl: String?,
    ): Result<JetchatUserProfileTomato>
    suspend fun clearConversationMessages(token: String, conversationId: Long): Result<Int>
}

@Composable
expect fun rememberJetchatServerGatewayTomato(baseUrl: String): JetchatServerGatewayTomato
