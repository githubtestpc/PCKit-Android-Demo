package com.pcsfg.pckit.chatkit.state.chatview

import android.content.Context
import com.pcsfg.pckit.chatkit.model.CachedFileReturn

sealed class ChatViewEvent {

    object ShowUserList: ChatViewEvent()
    object ShowInviteList: ChatViewEvent()
    data class ShowAllMessages(val showAllMessage:Int) : ChatViewEvent()
    object ShowDocumentBottomSheet: ChatViewEvent()
    object CloseDocumentBottomSheet: ChatViewEvent()
    data class UploadFile(val context: Context , val cachedFileReturn : CachedFileReturn) : ChatViewEvent()
    object LeaveChatRoom: ChatViewEvent()
    data class PostInvitePeople(val targetUser:String) : ChatViewEvent()
}