package com.pcsfg.pckit.chatkit.common.extension

import java.io.Serializable

data class ChatRoomInfo(
    val userID: String,
    val chatServerUrl: String,
    val chatRoomUrl: String,
    val chatRoomProfilePic: String,
    val chatRoomUserName: String,
    val chatRoomID: String,
    val chatRoomLeave: Int,
    var chatRoomShowAllMessage: Int,
    var target: String = "",
    var type:String = "",
    var serverVersion:String = "",
    var companyCode:String = "",
    var lang:String = "",
    var sysID:String,
    var platform:String = "3",
    var device:String = "5",
    var joinHead: String = "",
    var invite: String = "",
    var invppl: String = "",
    var source: String = ""

) : Serializable {

}