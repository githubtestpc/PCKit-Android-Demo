package com.pcsfg.pckit.chatkit.ui.inputview

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.pcsfg.pckit.BuildConfig
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.common.extension.CameraFileProvider
import com.pcsfg.pckit.chatkit.common.extension.FileCacheUtil
import com.pcsfg.pckit.chatkit.model.CachedFileReturn
import com.pcsfg.pckit.chatkit.state.chatview.ChatViewEvent
import com.pcsfg.pckit.chatkit.state.inputview.InputViewEvent
import com.pcsfg.pckit.chatkit.styles.inputview.BottomSheetButtonStyle
import com.pcsfg.pckit.chatkit.ui.theme.attachFileButtonColor
import com.pcsfg.pckit.chatkit.ui.theme.cameraButtonColor
import com.pcsfg.pckit.chatkit.ui.theme.photoLibraryButtonColor
import com.pcsfg.pckit.chatkit.ui.theme.voiceMessageButtonColor
import com.pcsfg.pckit.chatkit.viewmodel.chatview.ChatViewModel
import com.pcsfg.pckit.chatkit.viewmodel.inputview.InputViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class
)
@Composable
fun ChatInputView(scope: CoroutineScope, bottomSheetState:ModalBottomSheetState, chatInputViewModel: InputViewModel , chatViewModel: ChatViewModel) {

    val context = LocalContext.current

    val mCacheLocation = context.cacheDir.toString() + "/" + BuildConfig.LIBRARY_PACKAGE_NAME //+ ".images"

    var textFieldState by remember {
        mutableStateOf("")
    }

    var sendButtonState by remember {
        mutableStateOf(true)
    }

//    val relocation = remember { BringIntoViewRequester() }
//    val scope = rememberCoroutineScope()

    val cameraLaunch =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                println("Take Photo $success")

                if(success) {
                    val fc: FileCacheUtil by lazy {
                        FileCacheUtil(context)
                    }

                    runBlocking {
                        val fileName = fc.rotateImage(chatInputViewModel.selectedImageURI)

                        val cachedFileReturn = CachedFileReturn()
                        cachedFileReturn.fileName = fileName
                        cachedFileReturn.fileType = "jpg"

                        chatViewModel.onEvent(
                            ChatViewEvent.UploadFile(
                                context = context,
                                cachedFileReturn = cachedFileReturn
                            )
                        )
                        chatInputViewModel.onEvent(InputViewEvent.CloseDocumentBottomSheet)
                    }
                }
            }
        )

    val documentResult = remember { mutableStateOf<Uri?>(null)}
    val documentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            documentResult.value = it
            println ("Document : $it")

            if(it != null) {
                chatInputViewModel.onEvent(InputViewEvent.CacheDocument(uri = it , context = context))
                chatInputViewModel.onEvent(InputViewEvent.CloseDocumentBottomSheet)
            }
        }

    val galleryResult = remember { mutableStateOf<Uri?>(null)}
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            uri: Uri? ->
            galleryResult.value = uri
//            chatInputViewModel.selectedImageURI.value = uri.toString()
            if (uri != null) {
                chatInputViewModel.selectedImageURI = uri.lastPathSegment.toString()
            }
            println("URI Gallery : $uri")

            if(uri != null) {
                chatInputViewModel.onEvent(InputViewEvent.CacheImage(uri = uri, context = context))
                chatInputViewModel.onEvent(InputViewEvent.CloseDocumentBottomSheet)
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->

            if (isGranted) {
                println("Camera Permission Granted")
                chatInputViewModel.onEvent(InputViewEvent.ShowCamera)
            } else {
                println("Camera Permission Denied")
            }
        }

    val keyboardController = LocalSoftwareKeyboardController.current

    val attachFileButton = @Composable {
        IconButton(
            onClick = {
                println ("Attach File Button Hit!!!!")
                chatInputViewModel.onEvent(InputViewEvent.ShowDocumentBottomSheet)
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_attach_file),
                contentDescription = "",
                tint = voiceMessageButtonColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    val cameraButton = @Composable {
        IconButton(
            onClick = {
                println ("Camera Button Hit!!!!")

                when (PackageManager.PERMISSION_GRANTED) {

                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) -> {
                        println ("Camera Permission Granted From Chat Input View ")
                        chatInputViewModel.onEvent(InputViewEvent.ShowCamera)
                    }
                    else -> {
                        // Asking for permission
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo_camera),
                contentDescription = "",
                tint = voiceMessageButtonColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(vertical = 20.dp)
    ) {

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(horizontal = 10.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(10)),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextField(
                    value = textFieldState,
                    onValueChange = { textFieldState = it
                                        println("TextState : $textFieldState")
                                    },
                    placeholder = { Text(text = stringResource(id = R.string.ChatInputView_Message_PlaceHolder)) },
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(if (textFieldState.isNullOrEmpty()) 0.7f else 0.9f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )

                attachFileButton()

                if (textFieldState.isNullOrEmpty()) {
                    cameraButton()
                    sendButtonState = false
                }
                else
                {
                    sendButtonState = true
                }


            }

            SendButton(textFieldState = textFieldState, chatInputViewModel = chatInputViewModel , buttonState = sendButtonState)


        }

    }

    LaunchedEffect(key1 = context)
    {
        chatInputViewModel.uiEvent.collect { event ->
            when (event)
            {
                is InputViewEvent.ShowCamera -> {

                    val uri = CameraFileProvider.getImageUri(context)

                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) -> {
                            // Some works that require permission
                            println ("Camera Permission Granted From Chat Input View ")

                            cameraLaunch.launch(uri)
                            chatInputViewModel.selectedImageURI = uri.lastPathSegment.toString()

                        }
                        else -> {
                            // Asking for permission
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }

                is InputViewEvent.ShowDocumentBottomSheet -> {
                    keyboardController?.hide()
                    bottomSheetState.show()
                }

                is InputViewEvent.CloseDocumentBottomSheet -> {
                    bottomSheetState.hide()
                }

                is InputViewEvent.ShowDocumentDialog -> {
                    documentLauncher.launch(arrayOf("application/pdf" , "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", //.doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) //.xls & .xlsx
                }

                is InputViewEvent.ShowGallery -> {
                    galleryLauncher.launch("image/*")
                }

                is InputViewEvent.SendMessage -> {
                    chatViewModel.sendMessage(text = event.text)
                    textFieldState = ""
                }

                else -> {}
            }

        }
    }
}

@Composable
fun SendButton(textFieldState: String , chatInputViewModel: InputViewModel , buttonState : Boolean)
{
    Button(
        onClick = { /*TODO*/
            println("Enter Text $textFieldState")
            chatInputViewModel.onEvent(InputViewEvent.SendMessage(text = textFieldState.trimIndent()))
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = voiceMessageButtonColor),
        modifier = Modifier.size(55.dp),
        enabled = buttonState

    ) {
        Icon(
//                    painter = painterResource(id = R.drawable.ic_attach_file),
            imageVector = Icons.Filled.Send,
            contentDescription = "Icon",
            tint = Color.White
        )
//                Image(
//                    painter = painterResource(id = R.drawable.ic_voice_msg),
//                    contentDescription = "Voice Button",
//                    modifier = Modifier.clip(CircleShape)
//                )
    }
}



@Composable
fun FileBottomSheetContent(chatInputViewModel: InputViewModel)
{
    val buttonStyleList = listOf(
        BottomSheetButtonStyle(_buttonImage = R.drawable.ic_select_file , _buttonText = stringResource(R.string.ChatInputView_File) , _buttonBackgroundColor = attachFileButtonColor , _buttonClickAction = InputViewEvent.ShowDocumentDialog),
        BottomSheetButtonStyle(_buttonImage = R.drawable.ic_photo_camera , _buttonText = stringResource(R.string.ChatInputView_Camera), _buttonBackgroundColor = cameraButtonColor , _buttonClickAction = InputViewEvent.ShowCamera),
        BottomSheetButtonStyle(_buttonImage = R.drawable.ic_photo_library ,_buttonText = stringResource(R.string.ChatInputView_PhotoLibrary), _buttonBackgroundColor = photoLibraryButtonColor , _buttonClickAction = InputViewEvent.ShowGallery)
        )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f)
            .background(Color.White)
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
    )
    {
        items(buttonStyleList) { _button ->
            BottomSheetButton(button = _button , chatInputViewModel = chatInputViewModel)
        }
    }
}

@Composable
fun FileBottomSheetContent1(chatInputViewModel: InputViewModel)
{
    val buttonStyleList = listOf(
        BottomSheetButtonStyle(_buttonImage = R.drawable.ic_select_file , _buttonText = stringResource(R.string.ChatInputView_File) , _buttonBackgroundColor = attachFileButtonColor , _buttonClickAction = InputViewEvent.ShowDocumentDialog),
        BottomSheetButtonStyle(_buttonImage = R.drawable.ic_photo_camera , _buttonText = stringResource(R.string.ChatInputView_Camera), _buttonBackgroundColor = cameraButtonColor , _buttonClickAction = InputViewEvent.ShowCamera),
//        BottomSheetButtonStyle(_buttonImage = R.drawable.ic_photo_library ,_buttonText = stringResource(R.string.ChatInputView_PhotoLibrary), _buttonBackgroundColor = photoLibraryButtonColor , _buttonClickAction = InputViewEvent.ShowGallery)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f)
            .background(Color.White)
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
    )
    {
        items(buttonStyleList) { _button ->
            BottomSheetButton(button = _button , chatInputViewModel = chatInputViewModel)
        }
    }
}

@Composable
fun BottomSheetButton(button:BottomSheetButtonStyle , chatInputViewModel: InputViewModel)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top, modifier = Modifier.padding(10.dp)) {
        Button(
            onClick = { /*TODO*/
//            println("Enter Text ${textState.value.text}")
                      chatInputViewModel.onEvent(button.buttonClickAction)
            },
            modifier = Modifier.size(70.dp),
//            .clip(CircleShape),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = button.buttonBackgroundColor),

            ) {
            Icon(
                painter = painterResource(id = button.buttonImage),
//            imageVector = Icons.Filled,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(30.dp)

            )
        }
        Text(button.buttonText,fontSize = 14.sp,modifier = Modifier.padding(top = 10.dp), maxLines = 2 , textAlign = TextAlign.Center)
    }
}