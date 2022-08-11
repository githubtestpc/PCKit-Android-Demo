package com.pcsfg.pckit.chatkit.common.extension

import java.io.Serializable

data class SocketServerInfo(
    val chatServerUrl: String ,
    val serverVersion: String,
    val companyCode: String ,
    val lang: String,
    val sysID: String,
    val platform: String,
    val device: String,
) : Serializable {

}