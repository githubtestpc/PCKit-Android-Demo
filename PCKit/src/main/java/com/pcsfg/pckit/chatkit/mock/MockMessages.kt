package com.pcsfg.pckit.chatkit.mock

import com.pcsfg.pckit.chatkit.interfaces.ChatMessage
import com.pcsfg.pckit.chatkit.interfaces.ChatUser
import com.pcsfg.pckit.chatkit.model.ChatMessageKind
import java.util.*

class MockMessages()
{

        inner class ChatMessageItem() :ChatMessage{
                override var messageKind = ChatMessageKind.Text
                override var messageText = ""
                override var user: ChatUser = ChatUserItem()
                override var isSender = false
                override var date = Date()
                override var documentURL = ""
                override var imageURL = ""
                override var isSameSender = false
        }

        inner class ChatUserItem:ChatUser{
                override var userName = ""
                override var userID = ""
        }


}
//class MockMessages()
//{
//        var ChatMessageItem : List<ChatMessageItem> = emptyList()
//}



