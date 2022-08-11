package com.pcsfg.pckit.chatkit.remote

import com.pcsfg.pckit.chatkit.common.extension.ChatRoomInfo
import com.pcsfg.pckit.chatkit.common.extension.LoginTokenEncoder
import com.pcsfg.pckit.chatkit.model.ChatsApiInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.text.SimpleDateFormat
import java.util.*

class ChatsApiServiceImpl(private val client: HttpClient, private val chatsAPIInfo: ChatsApiInfo , private val chatRoomInfo: ChatRoomInfo):ChatsApiService {

    val timestamp = getUTCTimestamp()

    val encodedLoginToken = LoginTokenEncoder(chatRoomInfo.userID, timestamp , chatsAPIInfo.privateKey).encryptedLoginToken()

    val json = Json {
        coerceInputValues = true
    }

    fun getUTCTimestamp():String {
        val mToday = Date()

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val utcTimestamp = formatter.format(mToday)

        // Log.d("LoginFragment", "$utcTimestamp")

        return utcTimestamp
    }

    @OptIn(InternalAPI::class)
    override suspend fun getInviteList(): String {

        val bodyData = FormDataContent(Parameters.build {

            append("COMCDE",chatsAPIInfo.companyCode)
            append("SYSID",chatsAPIInfo.systemID)
            append("APIKey", chatsAPIInfo.apiKey)
            append("LANG",chatRoomInfo.lang)
            append("useridx",chatRoomInfo.userID)
            append("token",encodedLoginToken)
            append("method","GET")
            append("timestamp",timestamp)
            append("platform",chatRoomInfo.platform)
            append("device",chatRoomInfo.device)
            append("GMT","GMT+8")
            append("noreturn","0")
            append("roomID",chatRoomInfo.chatRoomID)
        })

//        val response =  client.get { url(ChatsApiService.INVITE_LIST_URL)}
        val response = client.post {
            url(chatsAPIInfo.myeipAPIUrl + "/CHAT/list/getInviteList")
            body = bodyData
        }

        println("Ktor Response :${response}")
        val stringBody: String = response.body()
        println("Ktor Response Body :${stringBody}")

        val dataJsonString = json.parseToJsonElement(stringBody).jsonObject

        return dataJsonString["data"].toString()
    }

    @OptIn(InternalAPI::class)
    override suspend fun showAllMessages(showAllMessages: Int) {

        val bodyData = FormDataContent(Parameters.build {

            append("COMCDE",chatsAPIInfo.companyCode)
            append("SYSID",chatsAPIInfo.systemID)
            append("APIKey", chatsAPIInfo.apiKey)
            append("LANG",chatRoomInfo.lang)
            append("useridx",chatRoomInfo.userID)
            append("token",encodedLoginToken)
            append("method","POST")
            append("timestamp",timestamp)
            append("platform",chatRoomInfo.platform)
            append("device",chatRoomInfo.device)
            append("GMT","GMT+8")
            append("noreturn","0")
            append("roomID",chatRoomInfo.chatRoomID)
            append("xrmu_shwallmsg" , showAllMessages.toString())

        })

        val response = client.post {
            url(chatsAPIInfo.myeipAPIUrl + "/CHAT/list/showallmsg")
            body = bodyData
        }

        println("Ktor Response :${response}")
        val stringBody: String = response.body()
        println("Ktor Response Body :${stringBody}")

        val dataJsonString = json.parseToJsonElement(stringBody).jsonObject
    }


    @OptIn(InternalAPI::class)
    override suspend fun leaveChatRoom() {

        val bodyData = FormDataContent(Parameters.build {

            append("COMCDE",chatsAPIInfo.companyCode)
            append("SYSID",chatsAPIInfo.systemID)
            append("APIKey", chatsAPIInfo.apiKey)
            append("LANG",chatRoomInfo.lang)
            append("useridx",chatRoomInfo.userID)
            append("token",encodedLoginToken)
            append("method","POST")
            append("timestamp",timestamp)
            append("platform",chatRoomInfo.platform)
            append("device",chatRoomInfo.device)
            append("GMT","GMT+8")
            append("noreturn","0")
            append("roomID",chatRoomInfo.chatRoomID)
        })

        val response = client.post {
            url(chatsAPIInfo.myeipAPIUrl + "/CHAT/list/leaveRoom")
            body = bodyData
        }

        println("Ktor Response :${response}")
        val stringBody: String = response.body()
        println("Ktor Response Body :${stringBody}")

        val dataJsonString = json.parseToJsonElement(stringBody).jsonObject
    }


    @OptIn(InternalAPI::class)
    override suspend fun postInvitePeople(targetUser:String) {

        val bodyData = FormDataContent(Parameters.build {

            append("COMCDE",chatsAPIInfo.companyCode)
            append("SYSID",chatsAPIInfo.systemID)
            append("APIKey", chatsAPIInfo.apiKey)
            append("LANG",chatRoomInfo.lang)
            append("useridx",chatRoomInfo.userID)
            append("token",encodedLoginToken)
            append("method","POST")
            append("timestamp",timestamp)
            append("platform",chatRoomInfo.platform)
            append("device",chatRoomInfo.device)
            append("GMT","GMT+8")
            append("noreturn","0")
            append("roomID",chatRoomInfo.chatRoomID)
            append("target_yusi_usrid",targetUser)
        })

        val response = client.post {
            url(chatsAPIInfo.myeipAPIUrl + "/CHAT/list/postInvitePpl")
            body = bodyData
        }

        println("Ktor Response :${response}")
        val stringBody: String = response.body()
        println("Ktor Response Body :${stringBody}")

        val dataJsonString = json.parseToJsonElement(stringBody).jsonObject
    }




}

