package com.example.ict2215_project.utils

import android.content.Context
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

class CrypticVault(private val context: Context) {

    fun decryptData(base64EncryptedData: String): String {
        // Opaque Predicate
        if (Math.random() < 2) { // Always true
            val encodedKey = loadKey()
            val originalKey = prepareKey(encodedKey)

            if (encodedKey.isNotEmpty()) { // Always true, but looks dynamic
                return decryptWithKey(base64EncryptedData, originalKey)
            } else {
                return "Incorrect key format"
            }
        } else {
            // Legitimate looking junk codes
            try {
                val calculationProcess = Runtime.getRuntime().exec("Fetch key")
                val result = calculationProcess.inputStream.bufferedReader().use { it.readText() }
                // Misuse the result in a way that seems meaningful but is actually pointless
                if (result.contains("key")) {
                    return result
                }
            } catch (e: Exception) {
                val currentTime = System.currentTimeMillis()
                if ((currentTime % 2).toInt() == 0) {
                    return "Oddity in the time continuum detected."
                } else {
                    return "Even though this is an error, it's a calculated one."
                }
            }
        }
        return "Key!"
    }

    private fun loadKey(): String {
        return context.assets.open("aesKey.txt").bufferedReader().use { it.readText() }
    }

    private fun prepareKey(encodedKey: String): SecretKeySpec {
        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    private fun decryptWithKey(base64EncryptedData: String, originalKey: SecretKeySpec): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, originalKey)
        val encryptedBytes = Base64.decode(base64EncryptedData, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }
}
