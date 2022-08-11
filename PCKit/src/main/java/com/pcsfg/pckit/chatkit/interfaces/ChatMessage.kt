package com.pcsfg.pckit.chatkit.interfaces

import com.pcsfg.pckit.chatkit.model.ChatMessageKind
import java.util.*

interface ChatMessage {

//    var user: ChatUser
//
//    /// Type of message
    val messageKind: ChatMessageKind

    /// Sender

    var messageText:String

    /// User
    var user:ChatUser

    /// To determine if user is the current user to properly align UI.
    var isSender: Boolean

    /// The date message sent.
    var date: Date

    ///Image URL
    var imageURL : String

    /// Document URL
    var documentURL : String

    /// Same as above cell sender
    var isSameSender : Boolean

}