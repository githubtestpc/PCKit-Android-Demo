package com.pcsfg.pckit.chatkit.socketmanager

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.GsonBuilder
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager(chatServerUrl : String) {

    lateinit var mSocket: Socket

    val chatServerUrl = chatServerUrl

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    var returnJson = ""

    var chatMessagesList : MutableState<String> = mutableStateOf("")

    private val onConnect = Emitter.Listener {
        Log.i("SocketMANAGERDEBUG", "connected")

        Log.i("SocketMANAGERDEBUG", "it: ${gson.toJson(it)}")


        // mSocket.emit("add user", "abc")

//        val m: MutableMap<Any?, Any?> = mutableMapOf()
//        m["version"] = "v2.1"
//        m["COMCDE"] = "PCSFG"
//        m["LANG"] = "zh-hk"
//        m["SYSID"] = "INDOME"
//        m["useridx"] = "kim.tam"
//        m["platform"] = "1"
//        m["device"] = "1"
//        m["token"] = ""
//        m["checksum"] = ""
//        m["GMT"] = "+08:00"
//        m["roomId"] = "RM00000039"
//        m["page"] = 1
//        m["row"] = 100
//
//        mSocket.emit("CHAT/chatmain/GetRoomMessage", JSONObject(m),
//            Ack { args: Array<Any> ->
//                val response = args[0] as JSONObject
//                Log.d("SocketMANAGERDEBUG", response.getString("status")) // "ok"
//            })
    }

    private val onDisconnect = Emitter.Listener {
        Log.i("SocketMANAGERDEBUG", "diconnected")
    }

    private val onConnectError = Emitter.Listener {
        Log.i("SocketMANAGERDEBUG", "Error connecting")
    }

    private val onGetRoomMessageReturn = Emitter.Listener {
        Log.i("SocketMANAGERDEBUG", "onGetRoomMessageReturn")

        Log.i("SocketMANAGERDEBUG", "it: ${gson.toJson(it)}")

        returnJson = gson.toJson(it)
        chatMessagesList.value = gson.toJson(it)

        println ("Socket Manager Inner List : $chatMessagesList.value")
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

    @Synchronized
    fun initSocket() {
        try {
//            mSocket = IO.socket("https://serviceuat1.pcsfg.com:8080/")
            mSocket = IO.socket(chatServerUrl)
//            mSocket = IO.socket("http://192.168.0.144:8080/")

            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
            // mSocket.on("login", onLoginReturn)
            // mSocket.on("new message", onNewMessageReturn)
            mSocket.on("GetRoomMessageReturn", onGetRoomMessageReturn)
//            mSocket.on("SendMessageSuccess", onSendMessageSuccess)
//            mSocket.on("SendMessageReturn", onSendMessageReturn)
            mSocket.connect()

            println ("mSocket Detail $mSocket")
        } catch (e: URISyntaxException) {

        }
    }



    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()

//        mSocket.on(Socket.EVEN ,Emitter.Listener { println("Connecting") })
        mSocket.on(Socket.EVENT_CONNECT, Emitter.Listener { println("Connected") })


    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }


    @Synchronized
    fun getChatRoomMessageList() {

        val m: MutableMap<Any?, Any?> = mutableMapOf()
        m["version"] = "v2.1"
        m["COMCDE"] = "PCSFG"
        m["LANG"] = "zh-hk"
        m["SYSID"] = "INDOME"
        m["useridx"] = "kim.tam"
        m["platform"] = "1"
        m["device"] = "1"
        m["token"] = ""
        m["checksum"] = ""
        m["GMT"] = "+08:00"
        m["roomId"] = "RM00000039"
        m["page"] = 1
        m["row"] = 100

        mSocket.emit("CHAT/chatmain/GetRoomMessage", JSONObject(m),
            Ack { args: Array<Any> ->
                val response = args[0] as JSONObject
                Log.d("SocketMANAGERDEBUG", response.getString("status")) // "ok"
            }
        )

    }
}