package com.pcsfg.pckit.chatkit.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomUser(
    val xcum_cusnicnam: String = "",
    var xcum_cuspic: String = "",
    val xcrm_romtarusrid: String = "",
    val xrmu_usrtyp: String = ""
)