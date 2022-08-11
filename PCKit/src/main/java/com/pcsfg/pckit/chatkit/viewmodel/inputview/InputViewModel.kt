package com.pcsfg.pckit.chatkit.viewmodel.inputview

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pcsfg.pckit.chatkit.common.extension.FileCacheUtil
import com.pcsfg.pckit.chatkit.state.chatview.ChatViewEvent
import com.pcsfg.pckit.chatkit.state.inputview.InputViewEvent
import com.pcsfg.pckit.chatkit.state.inputview.InputViewState
import com.pcsfg.pckit.chatkit.viewmodel.chatview.ChatViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class InputViewModel(_chatViewModel:ChatViewModel): ViewModel() {


        /**
         * The Input View UI state which holds all data
         */
        private var _inputViewState = mutableStateOf(InputViewState())
        public val inputViewState: State<InputViewState> = _inputViewState

        val uiEvent = MutableSharedFlow<InputViewEvent>()

        val chatViewModel = _chatViewModel

    //    public var selectedImageURI = mutableStateOf("content://media/external_primary/images/media/152463")
    //    public var selectedImageURI = mutableStateOf(Uri.EMPTY)
    //    public var selectedImageURI = Uri.EMPTY
        public var selectedImageURI = ""

    //    public val messageComposerState: StateFlow<MessageComposerState> = messageComposerController.state


        fun onEvent(event: InputViewEvent)
        {
            when (event) {
                is InputViewEvent.ShowCamera -> {
                   viewModelScope.launch {

                       uiEvent.emit(InputViewEvent.ShowCamera)
                   }
                }

                is InputViewEvent.ShowDocumentBottomSheet -> {
                    viewModelScope.launch {
//                        uiEvent.emit(InputViewEvent.ShowDocumentBottomSheet)
                        chatViewModel.uiEvent.emit(ChatViewEvent.ShowDocumentBottomSheet)
                    }
                }

                is InputViewEvent.CloseDocumentBottomSheet -> {
                    viewModelScope.launch {
//                        uiEvent.emit(InputViewEvent.CloseDocumentBottomSheet)
                        chatViewModel.uiEvent.emit(ChatViewEvent.CloseDocumentBottomSheet)
                    }
                }

                is InputViewEvent.ShowDocumentDialog -> {
                    viewModelScope.launch {
                        uiEvent.emit(InputViewEvent.ShowDocumentDialog)
                    }
                }

                is InputViewEvent.ShowGallery -> {
                    viewModelScope.launch {
                        uiEvent.emit(InputViewEvent.ShowGallery)
                    }
                }

                is InputViewEvent.CacheImage -> {
                    viewModelScope.launch {
                        val fc : FileCacheUtil by lazy {
                            FileCacheUtil(event.context)
                        }

                        if (event.uri != null) {
                            runBlocking {
                                val cachedFileReturn = fc.cacheImage(event.uri)
                                chatViewModel.onEvent(ChatViewEvent.UploadFile(context = event.context , cachedFileReturn = cachedFileReturn))
                            }
                        }
                    }
                }

                is InputViewEvent.CacheDocument -> {
                    viewModelScope.launch {
                        val fc : FileCacheUtil by lazy {
                            FileCacheUtil(event.context)
                        }

                        if (event.uri != null) {
                            runBlocking {
                                val cachedFileReturn = fc.cacheDocument(event.uri)
                                println ("Cache Document : $cachedFileReturn")
                                chatViewModel.onEvent(ChatViewEvent.UploadFile(context = event.context , cachedFileReturn = cachedFileReturn))
                            }
                        }
                    }
                }

                is InputViewEvent.ShowImage -> {
                    viewModelScope.launch {
                        uiEvent.emit(InputViewEvent.ShowImage)
                    }
                }

                is InputViewEvent.SendMessage -> {
                    viewModelScope.launch {
                        uiEvent.emit(InputViewEvent.SendMessage(text = event.text))
                    }
                }
            }
        }

//    @OptIn(ExperimentalPermissionsApi::class)
//    @Composable
//    fun cameraPermission() {
//
//        // Camera permission state
//        @OptIn(ExperimentalPermissionsApi::class)
//        val cameraPermissionState = rememberPermissionState(
//            Manifest.permission.CAMERA
//        )
//
//        when (cameraPermissionState.status) {
//
//            is PermissionStatus.Granted -> {
//                println ("Camera Permission Granted!!!!")
//            }
//            is PermissionStatus.Denied -> {
//                println ("Camera Permission Denied")
//            }
//
//        }
//
//
//    }
}