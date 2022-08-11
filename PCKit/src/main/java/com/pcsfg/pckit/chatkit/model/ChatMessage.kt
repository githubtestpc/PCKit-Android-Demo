package com.pcsfg.pckit.chatkit.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val sender: String = "",
    var message: String = "",
    val senderName: String = "",
    val senderPIC: String = "",
    var timestamp: String = "",
    var timestampDisplay: String = "",
    val originalMessage: String = "",
    val files:List<MessageFile>?,
    val msgID:String = "",
    val roomID:String = "",
    val tmpID:String = "",
    val systemmsg:Int = 0,
    val countread:Boolean = false,
    var messageKind:ChatMessageKind? = null,
    var isSender: Boolean? = false,
    /// Same as above cell sender
    var isSameSender : Boolean? = false,
    var messageStatus : ChatMessageStatus? = null,
    var cachedMessage:Boolean = false
)

@Serializable
data class MessageFile (
    val path:String? = "",
    val name:String,
    val url:String,
    val type:String,
    val data:String? = "",
    val width:Int? = 0,
    val height:Int? = 0,
    val tmpID:String = "",
    val videoimageName:String? = "",
    val videoimage:String? = "",
    val videoimageWidth:Int? = 0,
    val videoimageHeight:Int? = 0
)