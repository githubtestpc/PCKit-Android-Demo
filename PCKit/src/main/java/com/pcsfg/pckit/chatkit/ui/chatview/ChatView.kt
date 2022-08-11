package com.pcsfg.pckit.chatkit.ui.chatview

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.pcsfg.pckit.chatkit.ui.inputview.ChatInputView
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.common.extension.ChatRoomInfo
import com.pcsfg.pckit.chatkit.common.view.ImageWebView
import com.pcsfg.pckit.chatkit.common.view.VideoPlayerView
import com.pcsfg.pckit.chatkit.model.ChatViewBottomSheet
import com.pcsfg.pckit.chatkit.state.chatview.ChatViewEvent
import com.pcsfg.pckit.chatkit.styles.messagecells.ChatMessageCellStyle
import com.pcsfg.pckit.chatkit.ui.theme.topBarColor
import com.pcsfg.pckit.chatkit.viewmodel.chatview.ChatViewModel
import com.pcsfg.pckit.chatkit.viewmodel.inputview.InputViewModel
import timber.log.Timber

val GlobalCellStyle = compositionLocalOf<ChatMessageCellStyle> { error("No Cell Style Provided") }

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatView(chatViewModel: ChatViewModel,chatRoomInfo: ChatRoomInfo, activityKiller: () -> Unit) {

    Timber.plant(Timber.DebugTree())
    Timber.d("Chat View")

    val chatInputViewModel by remember {
        mutableStateOf(InputViewModel(_chatViewModel = chatViewModel))
    }

    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    val messageCellStyle = ChatMessageCellStyle()
    val navController = rememberNavController()

    var currentBottomSheet: ChatViewBottomSheet? by remember{
        mutableStateOf(null)
    }

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val keyboardController = LocalSoftwareKeyboardController.current

    NavHost(navController = navController, startDestination = NavRoutes.Home.route)
    {

        composable(NavRoutes.Home.route) {
            CompositionLocalProvider(GlobalCellStyle provides messageCellStyle) {

                ModalBottomSheetLayout(
                    sheetContent = {
                        Spacer(modifier = Modifier.height(1.dp))
                        currentBottomSheet?.let {
                            ChatBottomSheetView(
                                chatViewModel = chatViewModel,
                                chatInputViewModel = chatInputViewModel,
                                chatViewBottomSheet = it
                            )
                        }
//                        FileBottomSheetContent(chatInputViewModel = chatInputViewModel)
//                        FileBottomSheetContent1(chatInputViewModel = chatInputViewModel)
                    },
                    sheetState = modalBottomSheetState,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
//                    modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
//                        modifier = Modifier.padding(20.dp)

                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            ChatTopBar(
                                chatRoomInfo = chatRoomInfo,
                                chatViewModel = chatViewModel,
                                activityKiller = activityKiller,
                            )
                        },
                        bottomBar = {
                            ChatInputView(
                                scope = coroutineScope,
                                bottomSheetState = modalBottomSheetState,
                                chatInputViewModel = chatInputViewModel,
                                chatViewModel = chatViewModel
                            )
                        }
                    )
                    {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
//                                .fillMaxWidth()
//                                .fillMaxHeight(if (chatListPercentageState.value) 0.9f else 0.8f)
//                .verticalScroll(rememberScrollState(), reverseScrolling = true)
                                .background(Color.White)
//                                .padding(bottom = 10.dp),
                                .padding(it),
                            reverseLayout = true
                        )
                        {
                            items(chatViewModel.chatMessagesList) { message ->
                                ChatMessageCellContainer(
                                    message = message,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }

        composable(NavRoutes.PDF.route,
            arguments = listOf(
                navArgument("imageURL")
                {
                    type = NavType.StringType
                }
            )
        ) {
            ImageWebView(imageURL = it.arguments?.getString("imageURL")!!)
        }

        composable(NavRoutes.Image.route,
            arguments = listOf(
                navArgument("imageURL")
                {
                    type = NavType.StringType
                }
            )
        ) {
            ImageWebView(imageURL = it.arguments?.getString("imageURL")!!)
        }

        composable(
            NavRoutes.Video.route,
            arguments = listOf(
                navArgument("videoURL")
                {
                    type = NavType.StringType
                }
            )
        ) {
            VideoPlayerView(videoURL = it.arguments?.getString("videoURL")!! , navController = navController)
        }
    }

    LaunchedEffect(Unit, block = {
        chatViewModel.initSocket(chatRoomInfo)
    })

    LaunchedEffect(chatViewModel.chatMessagesList.size) {
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(key1 = modalBottomSheetState.currentValue) {
        chatViewModel.uiEvent.collect { event ->
            when (event) {
                is ChatViewEvent.ShowUserList -> {
                    currentBottomSheet = ChatViewBottomSheet.ChatRoomUserList
                    keyboardController?.hide()
                    modalBottomSheetState.show()
                }
                is ChatViewEvent.ShowInviteList -> {
//                    chatViewModel.getInviteList()
                    currentBottomSheet = ChatViewBottomSheet.ChatInviteList
                    keyboardController?.hide()
                    modalBottomSheetState.show()
                }
                is ChatViewEvent.ShowDocumentBottomSheet -> {
                    currentBottomSheet = ChatViewBottomSheet.Document
                    keyboardController?.hide()
                    modalBottomSheetState.show()
                }
                is ChatViewEvent.CloseDocumentBottomSheet -> {
                    modalBottomSheetState.hide()
                }
                else -> {}
            }
        }
    }

    DisposableEffect(key1 = activityKiller)
    {
        onDispose {
            chatViewModel.disconnectSocket()
        }
    }

//    if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
//        DisposableEffect(Unit) {
//            onDispose {
//                println("hidden")
//            }
//        }
//    }
}

@Composable
fun ChatTopBar(chatRoomInfo: ChatRoomInfo , chatViewModel: ChatViewModel, activityKiller: () -> Unit)
{
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .background(topBarColor)
                .height(50.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,

        ) {
            Button(
                onClick = {
                    Log.d(
                        "Jet Pack",
                        "Back Button Hit"
                    )
                    activityKiller()
                },
                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = colorResource(id = R.color.topBarColor),
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                ),
                elevation = null
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_back_no_shadow),
                    contentDescription = "Back Icon",
                )
            }

            Button(
                onClick = {
                    Log.d(
                        "Jet Pack",
                        "Profile Button"
                    )
                },
                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = colorResource(id = R.color.topBarColor),
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = null,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
            ){

                if(chatRoomInfo.chatRoomProfilePic == ""){
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_profile),
                        contentDescription = "Profile Icon",
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
                else {
                    AsyncImage(
                        model = chatRoomInfo.chatRoomProfilePic,
                        contentDescription = null,
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }

            Button(
                onClick = {
                    Log.d(
                        "Jet Pack",
                        "Profile Button"
                    )
                    chatViewModel.onEvent(ChatViewEvent.ShowUserList)
                },
                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = colorResource(id = R.color.topBarColor),
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                ),
                elevation = null,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(250.dp)
                    .padding(start = 10.dp)
            ){
                Text(chatRoomInfo.chatRoomUserName , textAlign = TextAlign.Start , maxLines = 2 , overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = {
                    Log.d(
                        "Jet Pack",
                        "More Button Hit"
                    )
                    showMenu = !showMenu
                },
                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = colorResource(id = R.color.topBarColor),
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                ),
                elevation = null,
                contentPadding = PaddingValues(0.dp),
//                modifier = Modifier.width(250.dp),
//                horizontal

            ){
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Localized description",
                    Modifier.padding(end = 8.dp)
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }){
                    DropdownMenuItem(onClick = {
                        chatViewModel.onEvent(ChatViewEvent.ShowInviteList)
                        showMenu = false
                    }) {
                        Text(text = stringResource(R.string.ChatView_ActionMenu_InviteToJoin))
                    }
                    if(chatRoomInfo.chatRoomLeave == 1)
                    {
                        DropdownMenuItem(onClick = {
                            chatViewModel.onEvent(ChatViewEvent.LeaveChatRoom)
                            activityKiller()
                            showMenu = false
                        }) {
                            Text(text = stringResource(R.string.ChatView_ActionMenu_LeaveRoom))
                        }
                    }
                    DropdownMenuItem(onClick = {
                        chatViewModel.onEvent(ChatViewEvent.ShowAllMessages(showAllMessage = chatRoomInfo.chatRoomShowAllMessage))
                        showMenu = false
                    }) {
                        if (chatRoomInfo.chatRoomShowAllMessage == 0) {
                            Text(text = stringResource(R.string.ChatView_ActionMenu_ShowAllMessage))
                        }
                        else
                        {
                            Text(text = stringResource(R.string.ChatView_ActionMenu_ShowRoomMessageOnly))
                        }
                    }
//                    DropdownMenuItem(onClick = { }) {
//                        Text(text = stringResource(R.string.ChatView_ActionMenu_LeaveRoom))
//                    }
                }
//                Text("VX" , textAlign = TextAlign.Start)
            }
//            Spacer(Modifier.width(30.dp))
        }
    }

//    LaunchedEffect(Unit, block = {
//        chatViewModel.uiEvent.collect { event ->
//            when (event) {
//                is ChatViewEvent.CloseActionMenu -> {
//                 showMenu = false
//                }
//                else -> {}
//            }
//        }
//    })
}

//@Preview(showBackground = true)
//@Composable
//fun ChatViewPreview() {
////    ComposeTheme {
////        val navController = rememberNavController()
////        val screen1ViewModel = Screen1ViewModel()
////
////        Screen1(navController, screen1ViewModel)
////    }
//    var messages : MutableList<MockMessages.ChatMessageItem> = mutableListOf()
////    var message = MockMessages.ChatMessageItem()
//    messages.add(MockMessages().ChatMessageItem())
//
//    ChatView(topBarTitle = "Customer", Messages = messages)
//}


sealed class NavRoutes(val route:String) {
    object Home : NavRoutes("home")
    object PDF : NavRoutes("pdf")
    object Image : NavRoutes("image/{imageURL}") {
        fun createRoute(imageURL:String) = "image/$imageURL"
    }
    object Video: NavRoutes("video/{videoURL}") {
        fun createRoute(videoURL:String) = "video/$videoURL"
    }
//    object Profile : NavRoutes("profile")
}