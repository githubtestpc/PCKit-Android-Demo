package com.pcsfg.pckit.chatkit.state.inputview

import android.content.Context
import android.net.Uri

sealed class InputViewEvent {

//    data class ShowCamera(val showCamera:Boolean): InputViewEvent()
    object ShowCamera: InputViewEvent()
    object ShowDocumentBottomSheet: InputViewEvent()
    object CloseDocumentBottomSheet: InputViewEvent()
    object ShowDocumentDialog: InputViewEvent()
    object ShowGallery: InputViewEvent()
    data class CacheImage(val uri: Uri, val context: Context) : InputViewEvent()
    data class CacheDocument(val uri: Uri, val context: Context) : InputViewEvent()
    object ShowImage: InputViewEvent()
    data class SendMessage(val text:String): InputViewEvent()
}