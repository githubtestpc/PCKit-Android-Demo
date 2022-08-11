package com.pcsfg.pckit.chatkit.model

import java.io.Serializable

data class ChatsApiInfo(
    val myeipAPIUrl:String = "",
    val companyCode:String = "",
    val systemID:String = "",
    val apiKey:String = "",
    val privateKey:String = "",
): Serializable {

}
