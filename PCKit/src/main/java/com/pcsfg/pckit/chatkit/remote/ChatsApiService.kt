package com.pcsfg.pckit.chatkit.remote

import com.pcsfg.pckit.chatkit.common.extension.ChatRoomInfo
import com.pcsfg.pckit.chatkit.model.ChatsApiInfo
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*

interface ChatsApiService {

    companion object {
        private const val API_BASE_URL = "https://myeipuat1.pcsfg.com/api/v2.1"

        const val INVITE_LIST_URL = "$API_BASE_URL/CHAT/list/getInviteList"
        const val DISC_URL = "$API_BASE_URL/disclaimer"

        fun create(_chatsAPIInfo: ChatsApiInfo , _chatRoomInfo: ChatRoomInfo): ChatsApiService {
            return ChatsApiServiceImpl(
                client = HttpClient(Android) {
                    // Logging
                    install(Logging) {
                        level = LogLevel.ALL
                    }

                    //JSON
                    install(ContentNegotiation) {
                        json
                    }

                    // Timeout
                    install(HttpTimeout) {
                        requestTimeoutMillis = 15000L
                        connectTimeoutMillis = 15000L
                        socketTimeoutMillis = 15000L
                    }
                    // Apply to all requests
                    defaultRequest {
                        url {
                            protocol = URLProtocol.HTTPS
                        }
                        // Parameter("api_key", "some_api_key")
                        // Content Type
//                        if (method != HttpMethod.Get) contentType(ContentType.Application.Json)
//                        accept(ContentType.Application.Json)
                    }
                },
                chatsAPIInfo = _chatsAPIInfo,
                chatRoomInfo = _chatRoomInfo
            )
        }

        private val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    }

    suspend fun getInviteList() :String

    suspend fun showAllMessages(showAllMessages: Int)

    suspend fun leaveChatRoom()

    suspend fun postInvitePeople(targetUser:String)
}