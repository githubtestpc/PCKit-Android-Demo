package com.pcsfg.pckit.chatkit.viewmodel.chatview

import android.content.Context
import android.net.UrlQuerySanitizer
import android.text.Html
import android.text.SpannableStringBuilder
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.pcsfg.pckit.BuildConfig
import com.pcsfg.pckit.chatkit.common.extension.ChatRoomInfo
import com.pcsfg.pckit.chatkit.remote.ChatsApiService
import com.pcsfg.pckit.chatkit.mock.MockMessages
import com.pcsfg.pckit.chatkit.model.*
import com.pcsfg.pckit.chatkit.state.chatview.ChatViewEvent
import io.ktor.util.*
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.set

class ChatViewModel(_chatRoomInfo: ChatRoomInfo , _chatsAPIInfo: ChatsApiInfo): ViewModel() {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    lateinit var mSocket: Socket

    private val _chatMessagesList = mutableStateListOf<ChatMessage>()
    val chatMessagesList: List<ChatMessage> = _chatMessagesList

    private var _chatRoomUserList = mutableStateListOf<ChatRoomUser>()
    val chatRoomUserList: List<ChatRoomUser> = _chatRoomUserList

    private var _chatInviteList = mutableStateListOf<ChatInviteUser>()
    val chatInviteList: List<ChatInviteUser> = _chatInviteList

    val uiEvent = MutableSharedFlow<ChatViewEvent>()
//    var chatMessagesList = mutableListOf<MockMessages.ChatMessageItem>()

//    var chatMessagesList : MutableState<List<MockMessages.ChatMessageItem>> = mutableStateOf(listOf())
//    var chatMessagesList = mutableListOf<ChatMessage>()
//    var chatMessagesList = mutableListOf<MockMessages.ChatMessageItem>()

    var chatMessagesListJson = ""

//    private val _chatMessagesListFlow = MutableStateFlow<kotlin.collections.mutableListOf<MockMessages.ChatMessageItem>>()

    var chatRoomInfo = _chatRoomInfo
//    var socketServerInfo = _socketServerInfo

    var chatsAPIInfo = _chatsAPIInfo

    private val chatsAPIService by lazy {
        ChatsApiService.create(_chatsAPIInfo = chatsAPIInfo , _chatRoomInfo = chatRoomInfo)
    }

    init {
//        socketManager.initSocket()
//        viewModelScope.launch {
////            chatMessagesList = generateMessage()
//           initSocket(_chatRoomInfo)
//        }
    }

    fun onEvent(event: ChatViewEvent) {
        when (event) {
            is ChatViewEvent.UploadFile -> {
                viewModelScope.launch {
//                    uiEvent.emit(InputViewEvent.ShowCamera)
                    uploadFile(event.context, event.cachedFileReturn)
                }
            }
            is ChatViewEvent.ShowUserList -> {
                viewModelScope.launch {
                    uiEvent.emit(ChatViewEvent.ShowUserList)
                }
            }
            is ChatViewEvent.ShowInviteList -> {
                viewModelScope.launch {
                    uiEvent.emit(ChatViewEvent.ShowInviteList)
                }
            }
            is ChatViewEvent.ShowAllMessages -> {
                viewModelScope.launch {
                    showAllMessages(event.showAllMessage)
                }
            }
            is ChatViewEvent.ShowDocumentBottomSheet -> {
                viewModelScope.launch {
                    uiEvent.emit(ChatViewEvent.ShowDocumentBottomSheet)
                }
            }
            is ChatViewEvent.CloseDocumentBottomSheet -> {
                viewModelScope.launch {
                    uiEvent.emit(ChatViewEvent.CloseDocumentBottomSheet)
                }
            }
            is ChatViewEvent.LeaveChatRoom -> {
                viewModelScope.launch {
                    leaveChatRoom()
                }
            }
            is ChatViewEvent.PostInvitePeople -> {
                viewModelScope.launch {
                    uiEvent.emit(ChatViewEvent.CloseDocumentBottomSheet)
                    postInvitePeople(event.targetUser)
                }
            }


            else -> {}
        }
    }

    private val onConnect = Emitter.Listener {
        Log.i("ChatViewModel", "connected")

        Log.i("ChatViewModel", "it: ${gson.toJson(it)}")

        joinChatRoom()
    }

    private val onDisconnect = Emitter.Listener {
        Log.i("Chat View Model", "diconnected")
    }

    private val onConnectError = Emitter.Listener {

        Log.i("Chat View Model", "Error connecting $it")
    }

    private val onJoinChatRoomReturn = Emitter.Listener {

        val response = it[0] as JSONObject
        Log.i("Chat View Model123", "onJoinChatRoomReturn")
        Log.i("Chat View Model123" , "REturn it : $it")
        Log.i("CHAT VIew Model123","Response JSON : ${response.getString("response")}")
        Log.i("CHAT VIew Model123","Response JSON : ${response.getString("version")}")
        Log.i("CHAT VIew Model123","Response JSON : ${response.getString("errno")}")
        Log.i("CHAT VIew Model123","Response JSON : ${response.getString("errMsg")}")
        Log.i("CHAT VIew Model123","Response JSON : ${response.getString("timestamp")}")
        Log.i("CHAT VIew Model123","Response JSON Data: ${response.getString("data")}")

        Timber.d("Response JSON Data 123: ${response.getString("data")}")
        val jsonString = response.getString("data")

        if(response.getBoolean("response")) {
            getChatRoomUserList()
            getChatRoomMessageList()
        }

    }

    private val onGetRoomUserListReturn = Emitter.Listener {

        val response = it[0] as JSONObject
        Log.i("Chat View Model 222324", "onGetRoomUserListReturn")
        Log.i("Chat View Model 222324" , "REturn it : $it")
        Log.i("Chat View Model 222324","Response JSON : ${response.getString("response")}")
        Log.i("Chat View Model 222324","Response JSON : ${response.getString("version")}")
        Log.i("Chat View Model 222324","Response JSON : ${response.getString("errno")}")
        Log.i("Chat View Model 222324","Response JSON : ${response.getString("errMsg")}")
        Log.i("Chat View Model 222324","Response JSON : ${response.getString("timestamp")}")
        Log.i("Chat View Model 222324","Response JSON Data: ${response.getString("data")}")

        Timber.d("Response JSON Data 222324: ${response.getString("data")}")
        val jsonString = response.getString("data")

        if(response.getBoolean("response")) {
            generateChatRoomUserList(jsonString)
        }

    }

    private val onGetRoomMessageReturn = Emitter.Listener {
        val response = it[0] as JSONObject
        Log.i("Chat View Model456", "onGetRoomMessageReturn")
        Log.i("Chat View Model456" , "REturn it : $it")
        Log.i("Chat View Model456","Response JSON : ${response.getString("response")}")
        Log.i("Chat View Model456","Response JSON : ${response.getString("version")}")
        Log.i("Chat View Model456","Response JSON : ${response.getString("errno")}")
        Log.i("Chat View Model456","Response JSON : ${response.getString("errMsg")}")
        Log.i("Chat View Model456","Response JSON : ${response.getString("timestamp")}")
        Log.i("Chat View Model456","Response JSON Data: ${response.getString("data")}")

        Timber.d("Response JSON Data 456: ${response.getString("data")}")
        val jsonString = response.getString("data")

        viewModelScope.launch {
            generateMessagesFromSocket(jsonString)
        }


//                val items =
//                generateMessageFromSocket(gson.toJson(it))
//        val m: MutableMap<Any?, Any?> = mutableMapOf()
//        m["version"] = "v2.1"
//        m["COMCDE"] = "PCSFG"
//        m["LANG"] = "zh-hk"
//        m["SYSID"] = "INDOME"
//        m["useridx"] = "1200038417"
//        m["platform"] = "1"
//        m["device"] = "1"
//        m["token"] = ""
//        m["checksum"] = ""
//        m["GMT"] = "+08:00"
//        m["roomId"] = "RM00000036"
//        m["message"] = "message 123"
//        m["tmpID"] = "temp_1"
//        m["files"] = ""
//        m["timestamp"] = DateTimeFormatter.getTimestamp()
//        m["timezone"] = 8
//        m["time"] = Date().time

//        mSocket.emit("CHAT/chatmain/SendMessage", JSONObject(m),
//            Ack { args: Array<Any> ->
//                val response = args[0] as JSONObject
//                Log.d("360DEBUG", response.getString("status")) // "ok"
//            })
    }

    private val onSendMessageReturn = Emitter.Listener {
        val response = it[0] as JSONObject
        Log.i("Chat View Model789", "onGetSendMessageReturn")
        Log.i("Chat View Model789" , "REturn it : $it")
        Log.i("Chat View Model789","Response JSON : ${response.getString("response")}")
        Log.i("Chat View Model789","Response JSON : ${response.getString("version")}")
        Log.i("Chat View Model789","Response JSON : ${response.getString("errno")}")
        Log.i("Chat View Model789","Response JSON : ${response.getString("errMsg")}")
        Log.i("Chat View Model789","Response JSON : ${response.getString("timestamp")}")
        Log.i("Chat View Model789","Response JSON Data: ${response.getString("data")}")

        Timber.d("Response JSON Data 789: ${response.getString("data")}")
        val jsonString = response.getString("data")

        var responseReturn = response.getBoolean("response")
//        if(response.getBoolean("response")) {
        if (responseReturn) {
            viewModelScope.launch {
                //Comment Below for Testing Send Error Case
                generateNewMessagesFromSocket(jsonString , false)
            }

        }
        else
        {
            viewModelScope.launch {
                generateSendFailedMessage("ABC")
            }
        }

    }

    private val onSendMessageSuccess = Emitter.Listener {
        val response = it[0] as JSONObject
        Log.i("Chat View Model101112", "onSendMessageSuccess")
        Log.i("Chat View Model101112" , "REturn it : $it")
        Log.i("Chat View Model101112","Response JSON : ${response.getString("response")}")
        Log.i("Chat View Model101112","Response JSON : ${response.getString("version")}")
        Log.i("Chat View Model101112","Response JSON : ${response.getString("errno")}")
        Log.i("Chat View Model101112","Response JSON : ${response.getString("errMsg")}")
        Log.i("Chat View Model101112","Response JSON : ${response.getString("timestamp")}")
        Log.i("Chat View Model101112","Response JSON Data: ${response.getString("data")}")

        Timber.d("Response JSON Data 101112: ${response.getString("data")}")
        val jsonString = response.getString("data")
//        viewModelScope.launch {
//            generateNewMessagesFromSocket(jsonString)
//        }
    }

    private val onSendFile = Emitter.Listener {
        val response = it[0] as JSONObject
        Log.i("Chat View Model 192021", "onSendReturnFile")
        Log.i("Chat View Model 192021" , "REturn it : $it")
        Log.i("Chat View Model 192021","Response JSON : ${response.getString("response")}")
        Log.i("Chat View Model 192021","Response JSON : ${response.getString("version")}")
        Log.i("Chat View Model 192021","Response JSON : ${response.getString("errno")}")
        Log.i("Chat View Model 192021","Response JSON : ${response.getString("errMsg")}")
        Log.i("Chat View Model 192021","Response JSON : ${response.getString("timestamp")}")
        //Log.i("Chat View Model 192021","Response JSON Data: ${response.getString("data")}")

//        Timber.d("Response JSON Data 192021: ${response.getString("data")}")
//        val jsonString = response.getString("data")
//        viewModelScope.launch {
//            generateNewMessagesFromSocket(jsonString)
//        }
    }

    private val onFinishedUpload = Emitter.Listener {
        val response = it[0] as JSONObject
        Log.i("Chat View Model161718", "onFinishedUpload")
        Log.i("Chat View Model161718" , "REturn it : $it")
        Log.i("Chat View Model161718","Response JSON : ${response.getString("name")}")
        Log.i("Chat View Model161718","Response JSON : ${response.getString("type")}")
        Log.i("Chat View Model161718","Response JSON : ${response.getString("data")}")
        Log.i("Chat View Model161718","Response JSON : ${response.getString("path")}")
        Log.i("Chat View Model161718","Response JSON : ${response.getString("tmpID")}")
//        Log.i("Chat View Model161718","Response JSON : ${response.getString("timestamp")}")
//        Log.i("Chat View Model161718","Response JSON Data: ${response.getString("data")}")

//        Timber.d("Response JSON Data 131415: ${response.getString("data")}")
        val jsonString = response.toString()
//        viewModelScope.launch {
//            generateNewMessagesFromSocket(jsonString)
//        }
        sendFileMessage(jsonString = jsonString)
    }

    private val onStartRecordReturn = Emitter.Listener {
        val response = it[0] as JSONObject
        Log.i("Chat View Model131415", "onSendStartFile")
        Log.i("Chat View Model131415" , "REturn it : $it")
        Log.i("Chat View Model131415","Response JSON : ${response.getString("response")}")
        Log.i("Chat View Model131415","Response JSON : ${response.getString("version")}")
        Log.i("Chat View Model131415","Response JSON : ${response.getString("errno")}")
        Log.i("Chat View Model131415","Response JSON : ${response.getString("errMsg")}")
        Log.i("Chat View Model131415","Response JSON : ${response.getString("timestamp")}")
        Log.i("Chat View Model131415","Response JSON Data: ${response.getString("data")}")

//        Timber.d("Response JSON Data 131415: ${response.getString("data")}")
        val jsonString = response.getString("data")
//        viewModelScope.launch {
//            generateNewMessagesFromSocket(jsonString)
//        }
    }



    var checkUser = ""
    var checkDate = ""
    private val pattern = "yyyy-MM-dd"
    val simpleDateFormat = SimpleDateFormat(pattern)
    val json = Json {
        coerceInputValues = true
    }

    //Function Start
    @Synchronized
    fun initSocket(chatRoomInfo: ChatRoomInfo) {
        try {

//            getInviteList()
            splitChatRoomParameter(url = chatRoomInfo.chatRoomUrl)

//            mSocket = IO.socket("https://serviceuat1.pcsfg.com:8080/")
            var options = IO.Options()
            options.randomizationFactor = 0.0
            options.reconnectionDelayMax = 1000
//            mSocket = IO.socket(chatRoomInfo.chatServerUrl , options)
            mSocket = IO.socket(chatRoomInfo.chatServerUrl)
//            mSocket = IO.socket(socketServerInfo.chatServerUrl)

            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
////        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
            mSocket.on("InitByDeviceReturn",onJoinChatRoomReturn)
//            // mSocket.on("new message", onNewMessageReturn)
            mSocket.on("GetRoomUserListReturn", onGetRoomUserListReturn)
            mSocket.on("GetRoomMessageReturn", onGetRoomMessageReturn)
            mSocket.on("SendMessageSuccess", onSendMessageSuccess)
            mSocket.on("SendMessageReturn", onSendMessageReturn)
            mSocket.on("SendFileSuccess" , onSendFile)
            mSocket.on("SendFileReturn",onSendFile)
            mSocket.on("StartRecordReturn" , onStartRecordReturn)
//            mSocket.on("CHAT/chatmain/SendFile" , onSendMessageReturn)
            mSocket.on("finishedUpload", onFinishedUpload)
            mSocket.connect()

            println ("mSocket Detail $mSocket")
        } catch (e: URISyntaxException) {

        }

        getInviteList()
    }

    @Synchronized
    fun disconnectSocket()
    {
        mSocket.disconnect()
        mSocket.off(Socket.EVENT_CONNECT, onConnect)
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
        mSocket.off("InitByDeviceReturn",onJoinChatRoomReturn)
        mSocket.off("GetRoomMessageReturn", onGetRoomMessageReturn)
//            mSocket.on("SendMessageSuccess", onSendMessageSuccess)
        mSocket.off("SendMessageReturn", onSendMessageReturn)
    }


    @Synchronized
    fun joinChatRoom(){

        val m: MutableMap<Any?, Any?> = mutableMapOf()
        m["version"] = chatRoomInfo.serverVersion
        m["COMCDE"] = chatRoomInfo.companyCode
        m["LANG"] = chatRoomInfo.lang
        m["SYSID"] = chatRoomInfo.sysID
        m["useridx"] = chatRoomInfo.userID
        m["platform"] = chatRoomInfo.platform
        m["device"] = chatRoomInfo.device
        m["token"] = ""
        m["checksum"] = ""
        m["GMT"] = "+08:00"
        m["roomId"] = chatRoomInfo.chatRoomID
        m["pc_account"] = chatRoomInfo.userID
        m["targetUser"] = chatRoomInfo.target
        m["joinhead"] = chatRoomInfo.joinHead
        m["targettype"] = chatRoomInfo.type
        m["invite"] = chatRoomInfo.invite
        m["invppl"] = chatRoomInfo.invppl
        m["source"] = chatRoomInfo.source


        mSocket.emit("SYSM/pclogin/InitByDevice", JSONObject(m),
            Ack { args: Array<Any> ->
                val response = args[0] as JSONObject
                Log.d("Chat View Model", response.getString("status")) // "ok"
            }
        )


    }

    @Synchronized
    fun getChatRoomUserList() {

        val m: MutableMap<Any?, Any?> = mutableMapOf()
        m["version"] = chatRoomInfo.serverVersion
        m["COMCDE"] = chatRoomInfo.companyCode
        m["LANG"] = chatRoomInfo.lang
        m["SYSID"] = chatRoomInfo.sysID
        m["useridx"] = chatRoomInfo.userID
        m["platform"] = chatRoomInfo.platform
        m["device"] = chatRoomInfo.device
        m["token"] = ""
        m["checksum"] = ""
        m["GMT"] = "+08:00"
        m["roomId"] = chatRoomInfo.chatRoomID
        m["pc_account"] = chatRoomInfo.userID
        m["targetUser"] = chatRoomInfo.target
        m["joinhead"] = ""
        m["targettype"] = chatRoomInfo.type
        m["invite"] = ""


        mSocket.emit("CHAT/chatmain/GetRoomUserList", JSONObject(m),
            Ack { args: Array<Any> ->
                val response = args[0] as JSONObject
                Log.d("Chat View Model", response.getString("status")) // "ok"
            }
        )
    }

    @Synchronized
    fun getChatRoomMessageList() {

        val m: MutableMap<Any?, Any?> = mutableMapOf()
        m["version"] = chatRoomInfo.serverVersion
        m["COMCDE"] = chatRoomInfo.companyCode
        m["LANG"] = chatRoomInfo.lang
        m["SYSID"] = chatRoomInfo.sysID
        m["useridx"] = chatRoomInfo.userID
        m["platform"] = chatRoomInfo.platform
        m["device"] = chatRoomInfo.device
        m["token"] = ""
        m["checksum"] = ""
        m["GMT"] = "+08:00"
        m["roomId"] = chatRoomInfo.chatRoomID
        m["page"] = 1
        m["row"] = 10

        mSocket.emit("CHAT/chatmain/GetRoomMessage", JSONObject(m),
            Ack { args: Array<Any> ->
                val response = args[0] as JSONObject
                Log.d("Chat View Model", response.getString("status")) // "ok"
            }
        )

    }

    private fun splitChatRoomParameter(url:String)
    {
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true
        sanitizer.parseUrl(url)

        val name = sanitizer.getValue("room")
        chatRoomInfo.target = sanitizer.getValue("target")
        chatRoomInfo.type = sanitizer.getValue("type")
    }

    private fun generateChatRoomUserList(jsonString: String)
    {
        _chatRoomUserList.clear()

        val resultList = json.decodeFromString<MutableList<ChatRoomUser>>(jsonString)

        resultList.forEach {  user ->
            _chatRoomUserList.add(user)
        }

//        _chatRoomUserList.forEachIndexed { index , user ->
//
//            println ("User Nic name :${user.xcum_cusnicnam}")
//            println ("User Pic : ${user.xcum_cuspic}")
//            println ("User Rom usrid : ${user.xcrm_romtarusrid}")
//            println ("User Type : ${user.xrmu_usrtyp}")
//        }
    }

    private fun generateChatRoomInviteList(jsonString: String)
    {
        _chatInviteList.clear()

        val resultList = json.decodeFromString<MutableList<ChatInviteUser>>(jsonString)

        resultList.forEach {  user ->
            _chatInviteList.add(user)
        }

//        _chatRoomUserList.forEachIndexed { index , user ->
//
//            println ("User Nic name :${user.xcum_cusnicnam}")
//            println ("User Pic : ${user.xcum_cuspic}")
//            println ("User Rom usrid : ${user.xcrm_romtarusrid}")
//            println ("User Type : ${user.xrmu_usrtyp}")
//        }
    }

    private fun generateMessagesFromSocket(jsonString :String) {

        _chatMessagesList.clear()

        val messages = json.decodeFromString<MutableList<ChatMessage>>(jsonString)

        messages.forEachIndexed { index, message ->

            message.timestamp = message.timestamp.replace(" GMT+0", "")

            val dateTimePattern = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val tempDateTime = dateTimePattern.parse(message.timestamp)

            val tempDate = simpleDateFormat.format(tempDateTime)


            if (index == 0) {
                // Check Above Message is same sender
                if(message.systemmsg == 0) {
                    checkUser = message.sender
                    message.isSameSender = false
                }

                var dateTimeMessage: ChatMessage = ChatMessage(
                    sender = "",
                    message = "",
                    senderName = "",
                    senderPIC = "",
                    timestamp = "",
                    originalMessage = "",
                    files = null,
                    msgID = "",
                    roomID = "",
                    tmpID = "",
                    systemmsg = 0,
                    countread = false,
                    messageKind = ChatMessageKind.DateTime,
                    isSender = false,
                    isSameSender = false
                )

                dateTimeMessage.timestamp = message.timestamp
                dateTimeMessage.timestampDisplay = tempDate
                checkDate = tempDate

                _chatMessagesList.add(0, dateTimeMessage)
            } else {

                var dateTimeMessage: ChatMessage = ChatMessage(
                    sender = "",
                    message = "",
                    senderName = "",
                    senderPIC = "",
                    timestamp = "",
                    originalMessage = "",
                    files = null,
                    msgID = "",
                    roomID = "",
                    tmpID = "",
                    systemmsg = 0,
                    countread = false,
                    messageKind = ChatMessageKind.DateTime,
                    isSender = false,
                    isSameSender = false
                )


                if (checkDate != tempDate) {
                    dateTimeMessage.timestamp = message.timestamp
                    dateTimeMessage.timestampDisplay = tempDate
                    _chatMessagesList.add(0, dateTimeMessage)
                    checkDate = tempDate

                    checkUser = "DateTime"
                }

                // Check Above Message is same sender
                message.isSameSender = checkUser == message.sender
                if(message.systemmsg != 1) {
                    checkUser = message.sender
                }

            }

            message.messageKind = ChatMessageKind.Text
            message.messageStatus = ChatMessageStatus.Synchronized

            message.message = message.message.replace("\n","<br />")
            val sequence = Html.fromHtml(message.message, Html.FROM_HTML_MODE_LEGACY)
            val strBuilder = SpannableStringBuilder(sequence)
            message.message = strBuilder.toString()

            if (message.sender == chatRoomInfo.userID) {
                message.isSender = true
            }

            if (message.files?.isEmpty() == true) {
                if(message.systemmsg == 1)
                {
                    message.messageKind = ChatMessageKind.SystemMessage
                }
                else {
                    message.messageKind = ChatMessageKind.Text
                }
            } else {
                for (file in message.files!!) {
                    when (file.type) {
                        "image/png" -> message.messageKind = ChatMessageKind.Image
                        "image/jpeg" -> message.messageKind = ChatMessageKind.Image
                        "application/pdf",
                        "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", //.doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" //.xls & .xlsx
                        -> message.messageKind = ChatMessageKind.Document
                        "video/quicktime", "video/mp4" -> message.messageKind =
                            ChatMessageKind.Video
                    }
                }
            }

            _chatMessagesList.add(0, message)
        }

        Timber.d("Message List from JSON Data: $messages")
        Timber.d("Message List from JSON Data View Model: $chatMessagesList")
    }



    private fun generateNewMessagesFromSocket(jsonString: String , cachedMessage:Boolean) {

        val message = json.decodeFromString<ChatMessage>(jsonString)

        if(message.tmpID != "") {
            searchMessage(message.tmpID)
        }

        message.timestamp = message.timestamp.replace(" GMT+0", "")

        val dateTimePattern = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val tempDateTime = dateTimePattern.parse(message.timestamp)

        val tempDate = simpleDateFormat.format(tempDateTime)

        var dateTimeMessage: ChatMessage = ChatMessage(
            sender = "",
            message = "",
            senderName = "",
            senderPIC = "",
            timestamp = "",
            originalMessage = "",
            files = null,
            msgID = "",
            roomID = "",
            tmpID = "",
            systemmsg = 0,
            countread = false,
            messageKind = ChatMessageKind.DateTime,
            isSender = false,
            isSameSender = false
        )


        if (checkDate != tempDate) {
            dateTimeMessage.timestamp = message.timestamp
            dateTimeMessage.timestampDisplay = tempDate
            _chatMessagesList.add(0, dateTimeMessage)
            checkDate = tempDate

            checkUser = "DateTime"
        }

//         Check Above Message is same sender
        message.isSameSender = checkUser == message.sender
        checkUser = message.sender


        message.messageKind = ChatMessageKind.Text
        message.messageStatus = ChatMessageStatus.Synchronized

        message.message = message.message.replace("\n","<br />")
        val sequence = Html.fromHtml(message.message, Html.FROM_HTML_MODE_LEGACY)
        val strBuilder = SpannableStringBuilder(sequence)
        message.message = strBuilder.toString()

        if (message.sender == chatRoomInfo.userID) {
            message.isSender = true
        }

        if (message.files?.isEmpty() == true) {
            if(message.systemmsg == 1)
            {
                message.messageKind = ChatMessageKind.SystemMessage
            }
            else {
                message.messageKind = ChatMessageKind.Text
            }
        } else {
            for (file in message.files!!) {
                when (file.type) {
                    "image/png" -> message.messageKind = ChatMessageKind.Image
                    "image/jpeg" -> message.messageKind = ChatMessageKind.Image
                    "application/pdf",
                    "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", //.doc & .docx
                    "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" //.xls & .xlsx
                    -> message.messageKind = ChatMessageKind.Document
                    "video/quicktime", "video/mp4" -> message.messageKind =
                        ChatMessageKind.Video
                }
            }
        }

        if(cachedMessage)
        {
            message.cachedMessage = true
        }

        _chatMessagesList.add(0, message)

        Timber.d("Message List from JSON Data: $message")
        Timber.d("Message List from JSON Data View Model: $chatMessagesList")
    }



    @Synchronized
    fun sendMessage(text:String)
    {
        val mToday = Date()

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = formatter.format(mToday)

        val uuid = UUID.randomUUID().toString().uppercase()

        generateCachedMessage(messageType = CachedMessageKind.Text , sender = chatRoomInfo.userID ,messageText = text , timestamp = timestamp , tmpID = uuid , roomID = chatRoomInfo.chatRoomID , fileName = "" , fileType = "")

        println ("Input Text : $text")
        val m: MutableMap<Any?, Any?> = mutableMapOf()
        m["version"] = chatRoomInfo.serverVersion
        m["COMCDE"] = chatRoomInfo.companyCode
        m["LANG"] = chatRoomInfo.lang
        m["SYSID"] = chatRoomInfo.sysID
        m["useridx"] = chatRoomInfo.userID
        m["platform"] = chatRoomInfo.platform
        m["device"] = chatRoomInfo.device
        m["token"] = ""
        m["checksum"] = ""
        m["GMT"] = "+08:00"
        m["roomId"] = chatRoomInfo.chatRoomID
        m["message"] = text
        m["tmpID"] = uuid
        m["files"] = ""
        m["timestamp"] = timestamp
        m["timezone"] = 8
        m["time"] = Date().time

        mSocket.emit("CHAT/chatmain/SendMessage", JSONObject(m),
            Ack { args: Array<Any> ->
                val response = args[0] as JSONObject
                Log.d("Chat View Model", response.getString("status")) // "ok"
            })
    }



    private fun generateSendFailedMessage(text: String) {

        var message = ChatMessage(files = listOf())
        message.message = text
        message.messageKind = ChatMessageKind.Text
        message.isSender = true

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        message.timestamp = dateFormatter.format(Date())

        _chatMessagesList.add(0, message)

    }


    @Synchronized
    fun sendFileMessage(jsonString: String)
    {
        val fileUpload = json.decodeFromString<FileUploadReturn>(jsonString)

        val mToday = Date()

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = formatter.format(mToday)

        val uuid = UUID.randomUUID().toString().uppercase()

        val mFiles: MutableMap<Any?, Any?> = mutableMapOf()
        mFiles["name"] = fileUpload.name
        mFiles["type"] = fileUpload.type
        mFiles["data"] = fileUpload.data
        mFiles["path"] = fileUpload.path
        mFiles["tmpID"] = fileUpload.tmpID

        val m: MutableMap<Any?, Any?> = mutableMapOf()
        m["version"] = chatRoomInfo.serverVersion
        m["COMCDE"] = chatRoomInfo.companyCode
        m["LANG"] = chatRoomInfo.lang
        m["SYSID"] = chatRoomInfo.sysID
        m["useridx"] = chatRoomInfo.userID
        m["platform"] = chatRoomInfo.platform
        m["device"] = chatRoomInfo.device
        m["token"] = ""
        m["checksum"] = ""
        m["GMT"] = "+08:00"
        m["roomId"] = chatRoomInfo.chatRoomID
        m["message"] = ""
        m["tmpID"] = fileUpload.tmpID
        m["files"] = arrayOf(mFiles)
        m["timestamp"] = timestamp
        m["timezone"] = 8
        m["time"] = Date().time

//        m["filename"] = "AndroidFileTest"


        mSocket.emit("CHAT/chatmain/SendFile", JSONObject(m),
            Ack { args: Array<Any> ->
                val response = args[0] as JSONObject
                Log.d("Chat View Model Send File Message", response.getString("status")) // "ok"
            })
    }

    @Synchronized
//    fun uploadFile(context: Context , fileName: String) {
        fun uploadFile(context: Context , cachedFileReturn: CachedFileReturn) {

        val mToday = Date()

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = formatter.format(mToday)

        val uuid = UUID.randomUUID().toString().uppercase()


//        val fileName = uri.lastPathSegment



        val mCacheLocation = context.cacheDir.toString() + "/" + BuildConfig.LIBRARY_PACKAGE_NAME
        val outputFile = File(mCacheLocation , cachedFileReturn.fileName)

//        val tempMessage = ChatMessage(sender = chatRoomInfo.userID , message = "" , senderName = "" , senderPIC = "" , timestamp = timestamp , timestampDisplay = "",originalMessage = "",)
        generateCachedMessage(messageType = CachedMessageKind.File , sender = chatRoomInfo.userID ,messageText = "" ,timestamp = timestamp , tmpID = uuid , roomID = chatRoomInfo.chatRoomID , fileName = cachedFileReturn.fileName , fileType = cachedFileReturn.fileType)

//        Timber.d(str)

        var str4 = FileUtils.readFileToByteArray(outputFile)

        val dataLen = str4.size

        println ("File Length : $dataLen")

        val fileMaxSize = 105
        val fileChunkSize = 1
        var fileExceedLimit = false
//        var chunks:[ByteArray] = ()
        val chunks = ArrayList<ByteArray>()
        var lastPiece = false

        val chunkSize = ((1024 * 1024) * fileChunkSize) // MB
        val fullChunks = dataLen / chunkSize // 1 Kbyte
        val mod = dataLen % 1024
        val totalChunks = fullChunks + (if (dataLen % (1024 * 1024) != 0) 1 else 0)

        for (chunkCounter in 0 until totalChunks) {

            var chunk = byteArrayOf()
            val chunkBase = chunkCounter * chunkSize
            var diff = chunkSize
            if (chunkCounter == totalChunks - 1)
            {
                diff = dataLen - chunkBase
            }

            val startIndex = chunkBase
            val endIndex = chunkBase + diff

//            let range:Range<Data.Index> = chunkBase..<(chunkBase + diff)
            chunk = str4.copyOfRange(startIndex,endIndex)

//            chunks.append(chunk)
            chunks.add(chunk)

        }
        val m: MutableMap<Any?, Any?> = mutableMapOf()
//        m["version"] = chatRoomInfo.serverVersion
//        m["COMCDE"] = chatRoomInfo.companyCode
//        m["LANG"] = chatRoomInfo.lang
//        m["SYSID"] = chatRoomInfo.sysID
//        m["useridx"] = chatRoomInfo.userID
//        m["platform"] = chatRoomInfo.platform
//        m["device"] = chatRoomInfo.device
//        m["token"] = ""
//        m["checksum"] = ""
//        m["GMT"] = "+08:00"
//        m["roomId"] = chatRoomInfo.chatRoomID
////        m["message"] = text
////        m["tmpID"] = uuid
//        m["files"] = ""
////        m["timestamp"] = timestamp
//        m["timezone"] = 8
//        m["time"] = Date().time
//        m["filename"] = "IMG_20220624_114820.jpg"
//        m["filename"] = "Image2.jpg"
//        m["last"] = false
//        m["filedata"] = ""
//        m["type"] = "image/jpeg"

        m["filename"] = cachedFileReturn.fileName
        m["last"] = false
        m["filedata"] = ""
        m["tmpID"] = uuid

        when (cachedFileReturn.fileType)
        {
            "jpg" -> m["type"] = "image/jpeg"
            "pdf" -> m["type"] = "application/pdf"
            "doc" -> m["type"] = "application/msword"
            "docx" -> m["type"] = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> m["type"] = "application/vnd.ms-excel"
            "xlsx" -> m["type"] = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> m["type"] = "application/vnd.ms-powerpoint"
            "pptx" -> m["type"] = "application/vnd.openxmlformats-officedocument.presentationml.presentation"

        }
//        m["type"] = "application/pdf"



//        m["filename"] = "AndroidFileTest"

//        mSocket.em("startRecord", JSONObject(m),
        mSocket.emit("startRecord", JSONObject(m),
            Ack { args: Array<Any> ->
                val response = args[0] as JSONObject
                Log.d("Chat View Model Start Record", response.getString("status")) // "ok"
            })

            chunks.forEachIndexed { index, chunk ->
                m["filedata"] = chunk

                if (index == chunks.lastIndex)
                {
                    m["last"] = true
                }

                mSocket.emit("streamingRecord", JSONObject(m),
                    Ack { args: Array<Any> ->
                        val response = args[0] as JSONObject
                        Log.d("Chat View Model Streaming Record", response.getString("status")) // "ok"
                    })
            }

        println ("Send Chunks Finished!!!")

//        mSocket.emit("streamingRecord", JSONObject(m),
//            Ack { args: Array<Any> ->
//                val response = args[0] as JSONObject
//                Log.d("Chat View Model Streaming Record", response.getString("status")) // "ok"
//            })
    }

    private fun searchMessage(tmpID: String)
    {
        val result = _chatMessagesList.indices.find { _chatMessagesList[it].tmpID == tmpID }

        println("Search Message Result Index : $result")

        if (result != null)
        {
            _chatMessagesList.removeAt(result)
        }
    }

    private fun generateCachedMessage(messageType:CachedMessageKind , sender:String, messageText:String ,timestamp :String, tmpID:String, roomID:String, fileName:String, fileType:String )
    {
        var tempFileType = ""
        when (fileType)
        {
            "jpg" -> tempFileType = "image/jpeg"
            "pdf" -> tempFileType = "application/pdf"
            "doc" -> tempFileType = "application/msword"
            "docx" -> tempFileType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> tempFileType = "application/vnd.ms-excel"
            "xlsx" -> tempFileType= "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> tempFileType = "application/vnd.ms-powerpoint"
            "pptx" -> tempFileType = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        }

        val fileJsonString = "{\"sender\":\"$sender\",\"message\":\"$messageText\",\"senderName\":\"\",\"senderPIC\":\"\",\"timestamp\":\"$timestamp\",\"originalMessage\":\"\",\"tmpID\":\"$tmpID\",\"msgID\":\"\",\"systemmsg\":0,\"roomID\":\"$roomID\",\"countread\":false,\"files\":[{\"name\":\"$fileName\",\"type\":\"$tempFileType\",\"data\":\"\",\"path\":\"\",\"width\":0,\"height\":0,\"url\":\"$fileName\"}]}"
        val textJsonString = "{\"sender\":\"$sender\",\"message\":\"$messageText\",\"senderName\":\"\",\"senderPIC\":\"\",\"timestamp\":\"$timestamp\",\"originalMessage\":\"\",\"tmpID\":\"$tmpID\",\"msgID\":\"\",\"systemmsg\":0,\"roomID\":\"$roomID\",\"countread\":false,\"files\":[]}"
//        val textJsonString = "{\"sender\":\"$sender\",\"message\":\"$messageText\",\"senderName\":\"\",\"senderPIC\":\"\",\"timestamp\":\"$timestamp\",\"originalMessage\":\"\",\"tmpID\":\"$tmpID\",\"msgID\":\"\",\"systemmsg\":0,\"roomID\":\"$roomID\",\"countread\":false,\"files\":[{\"name\":\"$fileName\",\"type\":\"$tempFileType\",\"data\":\"\",\"path\":\"\",\"width\":0,\"height\":0,\"url\":\"$fileName\"}]}"


        val jsonString =
            when (messageType) {

                CachedMessageKind.Text -> textJsonString
                CachedMessageKind.File -> fileJsonString
                else -> {""}
            }
//        val jsonString = "{\"sender\":\"$sender\",\"message\":\"\",\"senderName\":\"\",\"senderPIC\":\"\",\"timestamp\":\"$timestamp\",\"originalMessage\":\"\",\"tmpID\":\"$tmpID\",\"msgID\":\"\",\"systemmsg\":0,\"roomID\":\"$roomID\",\"countread\":true,\"files\":[{\"name\":\"$fileName\",\"type\":\"image\\/jpeg\",\"data\":\"\",\"path\":\"\",\"width\":0,\"height\":0,\"url\":\"$fileName\"}]}"

//        val t1 = "{\"sender\":\"kim.tam\",\"message\":\"\",\"senderName\":\"Kim\",\"senderPIC\":\"\",\"timestamp\":\"2022-07-12 14:50:03\",\"originalMessage\":\"\",\"tmpID\":\"EAAE2FFD-F7E9-4052-9595-2B53AF6FCB49\",\"msgID\":\"RS000000002897\",\"systemmsg\":0,\"roomID\":\"RM00000345\",\"countread\":true,\"files\":[{\"name\":\"Rotated_20220712_144931_2104293194660413341.jpg\",\"type\":\"image\\/jpeg\",\"data\":\"https:\\/\\/serviceuat1.pcsfg.com:8080\\/upload\\/202207121449318720.jpg\",\"path\":\"\\/var\\/www\\/virtual\\/serviceuat1\\/serviceuat1.pcsfg.com\\/node\\/service360uat\\/upload\\/202207121449318720.jpg\",\"width\":840,\"height\":1120,\"url\":\"https:\\/\\/pcsfgmkthk.oss-accelerate.aliyuncs.com\\/UAT\\/room\\/RM00000345\\/772_Rotated_20220712_144931_2104293194660413341.jpg\"}]}"
        generateNewMessagesFromSocket(jsonString = jsonString , cachedMessage = true)
    }


    fun File.toBase64(): String? {
        val result: String?
        inputStream().use { inputStream ->
            val sourceBytes = inputStream.readBytes()
            result = Base64.encodeToString(sourceBytes, Base64.NO_PADDING)
        }

        return result
    }

    fun getUTCTimestamp():String {
        val mToday = Date()

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val utcTimestamp = formatter.format(mToday)

        // Log.d("LoginFragment", "$utcTimestamp")

        return utcTimestamp
    }

    //    @OptIn(InternalAPI::class)
    @OptIn(InternalAPI::class)
    fun getInviteList() {

        runBlocking {
            launch {
                _chatInviteList.clear()
                val jsonString = chatsAPIService.getInviteList()
                println("Invite List Json : $json")

                generateChatRoomInviteList(jsonString)
            }
        }
    }

    private fun showAllMessages(showAllMessages: Int){

        println ("Show All Messages : $showAllMessages")
        var changeStatus = showAllMessages

        if (changeStatus == 0)
        {
            changeStatus = 1
        }
        else
        {
            changeStatus = 0
        }

        chatRoomInfo.chatRoomShowAllMessage = changeStatus

        runBlocking {
            launch{
                chatsAPIService.showAllMessages(showAllMessages = changeStatus)
            }
        }

    }

    private fun leaveChatRoom(){
        runBlocking {
            launch{
                chatsAPIService.leaveChatRoom()
            }
        }

    }

    private fun postInvitePeople(targetUser:String) {
        runBlocking {
            launch {
                chatsAPIService.postInvitePeople(targetUser = targetUser)
            }
        }
    }

//    private fun convertToBase64(attachment: File): String {
//
////        return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
//        val byteArray = attachment.readBytes()
//        val fileContent =
//
////        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//
//    }

    fun generateMessage(): MutableList<MockMessages.ChatMessageItem>
    {
        Timber.plant(Timber.DebugTree())

//    Log.d(
//        "Jet Pack",
//        "Generate Message Hit"
//    )
        Timber.d("Generate Message Hit")

        var messages : MutableList<MockMessages.ChatMessageItem> = mutableListOf()

        var sender = MockMessages().ChatUserItem()
        sender.userName = "Sender"
        sender.userID = "ABC"

        var chatBot = MockMessages().ChatUserItem()
        chatBot.userName = "Chat Bot"
        chatBot.userID = "DFG"

//    var checkUser = MockMessages().ChatUserItem()
        var checkUser = ""
        var checkDate = ""



        for (i in 1..10)
        {
            var message = MockMessages().ChatMessageItem()
            message.date = Date()
//        message.date = DateFormat.parse(LocalDate.now().plusDays(1).toString())
//        Log.d(ChatJetpack.CHATJETPACK.toString() , "Card ID: {$i}" )
//        Timber.d("Card ID: {$i}")

            when ((0..1).random())
            {
                0 -> {
                    message.isSender = false
                    message.user = chatBot
//                Log.d(ChatJetpack.CHATJETPACK.toString() , "Message isSender ${message.isSender}" )
                    if (i == 1)
                    {
                        checkUser = chatBot.userID
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Check User ID ${checkUser}" )
                        message.isSameSender = true
                    }
                    else
                    {
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Check User ID ${checkUser}" )
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Message User ID ${message.user.userID}" )
                        message.isSameSender = checkUser == message.user.userID
                        checkUser = message.user.userID
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Message is same Sender ${message.isSameSender}" )
                    }
                }
                1 -> {
                    message.isSender = true
                    message.user = sender
//                Log.d(ChatJetpack.CHATJETPACK.toString() , "Sender Message isSender ${message.isSender}" )
                    if (i == 1)
                    {
                        checkUser = sender.userID
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Sender Check User ID ${checkUser}" )
                        message.isSameSender = true
                    }
                    else {
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Sender Check User ID ${checkUser}" )
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Sender Message User ID ${message.user.userID}" )
                        message.isSameSender = checkUser == message.user.userID
                        checkUser = message.user.userID
//                    Log.d(ChatJetpack.CHATJETPACK.toString() , "Sender Message is same Sender ${message.isSameSender}" )
                    }
                }
            }




            when ((0..4).random()) {
                0 -> {
                    message.messageKind = ChatMessageKind.Text
                    message.messageText = """
                    www.google.com is a website without https
                    with https https://www.google.com/search?q=sample
                    +852 9806 9940 is a sample phone number (note: phone number linking depends on location, and is same as TextView's autoLink)
                    Sample email address sample@gmail.com ,
                    Sample Address 11/F, Lee Garden Five, 18 Hysan Avenue, Causeway Bay, Hong Kong ,
                    👍🏻👍🏻 🥸🏉
                """.trimIndent()
                    message.messageText = """
                 你好嗎 👍🏻👍🏻 🥸🏉你
                 你好嗎 👍🏻👍🏻 🥸🏉你
                 asjdkfjsdkfjdsksjdhfjsdhfjdshfjdsklfkdslfklsdf
                """.trimIndent()
                }
                1 ->
                {
                    message.messageKind = ChatMessageKind.ImageText
                    message.messageText = """
                    www.google.com is a website without https
                    with https https://www.google.com/search?q=sample
                    +852 9806 9940 is a sample phone number (note: phone number linking depends on location, and is same as TextView's autoLink)
                    Sample email address sample@gmail.com ,
                    Sample Address 11/F, Lee Garden Five, 18 Hysan Avenue, Causeway Bay, Hong Kong ,
                    👍🏻👍🏻 🥸🏉
                """.trimIndent()
                    when ((0..3).random()) {
                        0 -> message.imageURL = "https://browsecat.net/sites/default/files/4k-vertical-wallpapers-98596-690003-9958206.png"
                        1 -> message.imageURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/PRD/room/RM00000039/135_image.jpg"
                        2 -> message.imageURL = "https://images8.alphacoders.com/712/712496.jpg"
                        3 -> message.imageURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000041/285_IMG_9568.PNG"
                    }

                }
                2 -> {
                    message.messageKind = ChatMessageKind.Image
                    when ((0..3).random()) {
                        0 -> message.imageURL = "https://browsecat.net/sites/default/files/4k-vertical-wallpapers-98596-690003-9958206.png"
                        1 -> message.imageURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/PRD/room/RM00000039/135_image.jpg"
                        2 -> message.imageURL = "https://images8.alphacoders.com/712/712496.jpg"
                        3 -> message.imageURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000041/285_IMG_9568.PNG"
                    }
                }
                3 -> {
                    message.messageKind = ChatMessageKind.Document
//                message.messageText = "ABChdjhfjsdhfjdshfjshfjhdsjfhdsjfsdjfdhsjfhsjdfhdsjfhdsj.pdf"
                    message.messageText = "ABshdfkjskafjdksjfksdjkfdsjkfjdskjsfk.pdf"
                    message.documentURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000039/145_CICN300599221_895591.pdf"
                }
                4 -> {
                    message.messageKind = ChatMessageKind.Video
                    message.messageText = "ABChdjhfjsdhfjdshfjshfjhdsjfhdsjfsdjfdhsjfhsjdfhdsjfhdsj.pdf"
                    message.documentURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000039/145_CICN300599221_895591.pdf"
//                message.imageURL = "https://pcsfgmkthk.oss-accelerate.aliyuncs.com/UAT/room/RM00000041/285_IMG_9568.PNG"
                    message.imageURL = "https://images8.alphacoders.com/712/712496.jpg"
                }

            }
//    message.messageKind = ChatMessageKind.Image
//        message.messageText = """
//                    www.google.com is a website without https
//                    with https https://www.google.com/search?q=sample
//                    +852 9806 9940 is a sample phone number (note: phone number linking depends on location, and is same as TextView's autoLink)
//                    Sample email address sample@gmail.com ,
//                    Sample Address 11/F, Lee Garden Five, 18 Hysan Avenue, Causeway Bay, Hong Kong ,
//                    👍🏻👍🏻 🥸🏉
//                """.trimIndent()

//            Log.d(ChatJetpack.CHATJETPACK.toString() , "Message Kind ${message.messageKind}" )

            val pattern = "yyyy-MM-dd"
            val simpleDateFormat = SimpleDateFormat(pattern)

            val dayBefore = Date(message.date.time - 24 * 60 * 60 * 1000)
            val twoDayBefore =  Date(message.date.time - 2 * 24 * 60 * 60 * 1000)
            val dayAfter = Date(message.date.time + 24 * 60 * 60 * 1000)
            val twoDayAfter = Date(message.date.time + 2 * 24 * 60 * 60 * 1000)

            if( i<= 1)
            {
                message.date = twoDayBefore
            }
            else if (i in 2..30)
            {
                message.date = dayBefore
            }
            else if (i in 31..60)
            {
                message.date = Date()
            }
            else if (i in (61..99))
            {
                message.date = dayAfter
            }
            else if (i>99)
            {
                message.date = twoDayAfter
            }

            if(i==1)
            {
                var dateTimeMessage = MockMessages().ChatMessageItem()
                dateTimeMessage.messageKind = ChatMessageKind.DateTime

                checkDate = simpleDateFormat.format(message.date)
                dateTimeMessage.date = message.date
                messages.add(0 , dateTimeMessage)
            }
            else
            {
                val tempDate = simpleDateFormat.format(message.date)
                var dateTimeMessage = MockMessages().ChatMessageItem()
                dateTimeMessage.messageKind = ChatMessageKind.DateTime

                if(checkDate != tempDate)
                {
                    dateTimeMessage.date = message.date
                    messages.add(0 , dateTimeMessage)
                    checkDate = tempDate
                }
            }

//        messages.add(0,dateTimeMessage)
            messages.add(0,message)

        }
        return messages
    }

}