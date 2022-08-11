package com.pcsfg.pckit.chatkit.ui.components.messagecells

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pcsfg.pckit.R
import com.pcsfg.pckit.chatkit.model.ChatInviteUser
import com.pcsfg.pckit.chatkit.model.ChatMessage
import com.pcsfg.pckit.chatkit.model.ChatRoomUser

@Composable
fun AvatarView(message:ChatMessage)
{
    Column(modifier = Modifier.padding(end = 5.dp))
    {
        if (!message.isSender!!) {
            if (message.senderPIC == "") {
                Image(
                    painter = painterResource(id = R.drawable.ic_user_profile),
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(30.dp)
                )
            } else {
                AsyncImage(
                    model = message.senderPIC,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(30.dp)
                )
            }
        }
    }
}

@Composable
fun ChatUserListAvatarView(user:ChatRoomUser)
{
    Column(modifier = Modifier.padding(end = 5.dp))
    {

        if (user.xcum_cuspic == "") {
            Image(
                painter = painterResource(id = R.drawable.ic_user_profile),
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(25.dp)
            )
        } else {
            AsyncImage(
                model = user.xcum_cuspic,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(25.dp)
            )
        }
    }
}


@Composable
fun ChatInviteUserListAvatarView(user:ChatInviteUser)
{
    Column(modifier = Modifier.padding(end = 5.dp))
    {

        if (user.yusi_usrpic == "") {
            Image(
                painter = painterResource(id = R.drawable.ic_user_profile),
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(45.dp)
            )
        } else {
            AsyncImage(
                model = user.yusi_usrpic,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(45.dp)
            )
        }
    }
}