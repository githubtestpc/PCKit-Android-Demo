package com.pcsfg.pckit.chatkit.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatInviteUser(
    val xdul_idx: String = "",
    var yusi_comcde: String = "",
    val yusi_usrid: String = "",
    val xdul_usrtyp: String = "",
    val xdul_usrnth: String = "",
    val xdul_usrdsc: String = "",
    val yusi_usrnicnam: String = "",
    val yusi_usrpic: String = ""
)