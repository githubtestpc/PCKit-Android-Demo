package com.pcsfg.pckit.chatkit.common.extension

import android.util.Base64
import kotlin.experimental.xor

class LoginTokenEncoder(private val useridx: String, private val timestamp: String , private val privateKey:String) {
    companion object {
        // which is private key stored in indomedev.SYSMAPIUSR of "INDOME"
        private const val ENCRYPTION_KEY = "56285304f3a95b58b58428fd438d0aff"
    }


    fun encryptedLoginToken(): String {
        val base64Key = Base64.encodeToString(ENCRYPTION_KEY.toByteArray(), Base64.NO_WRAP)
        val combinedData = useridx + "_" + timestamp
        val base64CombinedData = Base64.encodeToString(combinedData.toByteArray(), Base64.NO_WRAP)

        val index = base64CombinedData.length % base64Key.length
        val keyFrontSubstring = base64Key.substring(index)
        val keyRearSubstring = base64Key.substring(0, index)
        val xorFactor = keyFrontSubstring + keyRearSubstring

        val base64PasswordBytes = base64CombinedData.toByteArray()
        val xorFactorBytes = xorFactor.toByteArray()
        val encryptedData = ByteArray(base64PasswordBytes.size)

        for (i in base64PasswordBytes.indices) {
            encryptedData[i] = base64PasswordBytes[i] xor xorFactorBytes[i % xorFactorBytes.size]
        }

        return Base64.encodeToString(encryptedData, Base64.NO_WRAP)
    }
}