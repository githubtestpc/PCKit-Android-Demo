package com.pcsfg.pckit.chatkit.ui.chatview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.model.ChatInviteUser
import com.pcsfg.pckit.chatkit.state.chatview.ChatViewEvent
import com.pcsfg.pckit.chatkit.ui.components.messagecells.ChatInviteUserListAvatarView
import com.pcsfg.pckit.chatkit.viewmodel.chatview.ChatViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatInviteListView(chatViewModel: ChatViewModel)
{
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = stringResource(R.string.ChatView_InviteList_Header),
            fontSize = 20.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .background(Color.White)
                .nestedScroll(rememberNestedScrollInteropConnection())

        )
        {
            items(chatViewModel.chatInviteList) { user ->
                ChatInviteUserRow(user = user, chatViewModel = chatViewModel)
            }
        }
    }

}

@Composable
fun ChatInviteUserRow(user: ChatInviteUser , chatViewModel: ChatViewModel)
{
    Row(
        modifier = Modifier.padding(20.dp)
        .clickable(
            onClick = {
                chatViewModel.onEvent(ChatViewEvent.PostInvitePeople(targetUser = user.yusi_usrid))
//                imageOnClick(imageURL = imageURL, navController = navController)
            }
        ),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        ChatInviteUserListAvatarView(user = user)
        Text(text = user.yusi_usrnicnam,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(10.dp))
    }

}