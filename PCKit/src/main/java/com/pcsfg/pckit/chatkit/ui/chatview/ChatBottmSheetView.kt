package com.pcsfg.pckit.chatkit.ui.chatview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.ui.inputview.FileBottomSheetContent
import com.pcsfg.pckit.chatkit.model.ChatRoomUser
import com.pcsfg.pckit.chatkit.model.ChatViewBottomSheet
import com.pcsfg.pckit.chatkit.ui.components.messagecells.ChatUserListAvatarView
import com.pcsfg.pckit.chatkit.viewmodel.chatview.ChatViewModel
import com.pcsfg.pckit.chatkit.viewmodel.inputview.InputViewModel

@Composable
fun ChatBottomSheetView(chatViewModel: ChatViewModel , chatInputViewModel: InputViewModel , chatViewBottomSheet: ChatViewBottomSheet)
//fun FileBottomSheetContent(chatInputViewModel: InputViewModel)
{
        when (chatViewBottomSheet) {
//            ChatViewBottomSheet.ChatRoomUserList -> FileBottomSheetContent1(chatInputViewModel = chatInputViewModel)
            ChatViewBottomSheet.ChatRoomUserList -> ChatRoomUserListView(chatViewModel = chatViewModel)
            ChatViewBottomSheet.ChatInviteList -> ChatInviteListView(chatViewModel = chatViewModel)
            ChatViewBottomSheet.Document -> FileBottomSheetContent(chatInputViewModel = chatInputViewModel)
            else -> {}
        }

}


@Composable
fun ChatRoomUserListView(chatViewModel: ChatViewModel)
{
    val listState = rememberLazyListState()

    val sizeText = chatViewModel.chatRoomUserList.size.toString() + " " + stringResource(R.string.ChatView_ChatUserListHeader)

    Text(text = sizeText,
        Modifier.padding(20.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .background(Color.White)
    )
    {
        items(chatViewModel.chatRoomUserList) { user ->
            ChatRoomUserRow(user = user)
        }
    }
}

@Composable
fun ChatRoomUserRow(user:ChatRoomUser)
{
    Row(modifier = Modifier.padding(20.dp))
    {
        ChatUserListAvatarView(user = user)
        Text(text = user.xcum_cusnicnam)
    }
}
