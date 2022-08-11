package com.pcsfg.pckit.chatkit.model

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadReturn(
    val name:String = "",
    val type:String = "",
    val data:String = "",
    val path:String = "",
    val tmpID:String = ""
)


@Serializable
data class CachedFileReturn (
    var fileName:String = "",
    var fileType:String = ""
)