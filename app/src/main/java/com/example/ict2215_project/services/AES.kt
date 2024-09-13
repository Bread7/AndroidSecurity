package com.example.ict2215_project.services

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import android.util.Log
import java.io.IOException
import java.nio.ByteBuffer
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec

class AES {
    private val keyStore: KeyStore

    init {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
    }

    @Throws(Exception::class)
    fun encrypt(plaintext: ByteArray?, alias: String?): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))
        val cipherText = cipher.doFinal(plaintext)
        val iv = cipher.iv
        return ByteBuffer.allocate(iv.size + cipherText.size)
            .put(iv)
            .put(cipherText)
            .array()
    }

    @Throws(Exception::class)
    fun decrypt(cText: ByteArray?, alias: String?): ByteArray {
        val bb = ByteBuffer.wrap(cText)
        val iv = ByteArray(GCM_IV_LENGTH)
        bb[iv]
        val cipherText = ByteArray(bb.remaining())
        bb[cipherText]
        return decrypt(cipherText, alias, iv)
    }

    @Throws(Exception::class)
    fun decrypt(cText: ByteArray?, alias: String?, iv: ByteArray?): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), GCMParameterSpec(TAG_LENGTH_BIT, iv))
        return cipher.doFinal(cText)
    }

    fun generateIV(): ByteArray {
        val iv = ByteArray(GCM_IV_LENGTH)
        secureRandom.nextBytes(iv)
        return iv
    }

    // used to generate per file key
    @Throws(
        NoSuchAlgorithmException::class,
        UnrecoverableEntryException::class,
        KeyStoreException::class,
        NoSuchProviderException::class,
        InvalidAlgorithmParameterException::class
    )
    fun getSecretKey(alias: String?): SecretKey {

        // Look in keystore
        val entry = keyStore.getEntry(alias, null)
        // If the entry exists and is an instance of KeyStore.SecretKeyEntry, proceed
        if (entry is KeyStore.SecretKeyEntry) {
            val factory = SecretKeyFactory.getInstance(entry.secretKey.algorithm, "AndroidKeyStore")
            try {
                val info = factory.getKeySpec(entry.secretKey, KeyInfo::class.java) as KeyInfo
                Log.d("Key Hardware", info.isInsideSecureHardware.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return entry.secretKey
        } else {
            // The key does not exist, generate a new one
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            val builder = KeyGenParameterSpec.Builder(
                alias!!,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            
            // Other parameters settings omitted for brevity

            var spec: AlgorithmParameterSpec = builder.build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // attempt to use TEE
                try {
                    builder.setIsStrongBoxBacked(true)
                    spec = builder.build()
                    keyGenerator.init(spec)
                    return keyGenerator.generateKey()
                } catch (e: StrongBoxUnavailableException) {
                    builder.setIsStrongBoxBacked(false)
                    spec = builder.build()
                    keyGenerator.init(spec)
                    e.printStackTrace()
                }
            }
            keyGenerator.init(spec)
            return keyGenerator.generateKey()
        }


        // if (key != null) {
        //     val factory = SecretKeyFactory.getInstance(key.secretKey.algorithm, "AndroidKeyStore")
        //     try {
        //         val info = factory.getKeySpec(key.secretKey, KeyInfo::class.java) as KeyInfo
        //         Log.d("Key Hardware", info.isInsideSecureHardware.toString())
        //     } catch (e: Exception) {
        //         e.printStackTrace()
        //     }
        //     return key.secretKey
        // }

        // // If cannot find, then create new one
        // val keyGenerator = KeyGenerator
        //     .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        // val builder = KeyGenParameterSpec.Builder(
        //     alias!!,
        //     KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        // )
        //     .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        //     .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        // var spec: AlgorithmParameterSpec
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        //     // attempt to use TEE
        //     try {
        //         builder.setIsStrongBoxBacked(true)
        //         spec = builder.build()
        //         keyGenerator.init(spec)
        //         return keyGenerator.generateKey()
        //     } catch (e: StrongBoxUnavailableException) {
        //         builder.setIsStrongBoxBacked(false)
        //         e.printStackTrace()
        //     }
        // }
        // spec = builder.build()
        // keyGenerator.init(spec)
        // return keyGenerator.generateKey()
    }

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val GCM_IV_LENGTH = 12
        private const val AES_KEY_SIZE = 256
        private const val TAG_LENGTH_BIT = 128
        private val fallbackKey =
            hexToBytes("c5c1180464181c7b010400c3b2063a42cd7c8ce03f9cf47522dbd942041614f5fc42181320be085707639bf4a2a2b61d164cb948ec2325dd65501bac83b0fb418f2d6061ce0553696857a63a1f146b9d39427d74ba288f8197e1c913bfcc07f92ac1ab37f489279c92a4187fc0dfafc645a325e150d835d3c22b971745e95d3b")
        private val secureRandom = SecureRandom()

        @get:Throws(
            KeyStoreException::class,
            CertificateException::class,
            NoSuchAlgorithmException::class,
            IOException::class
        )
        var instance: AES? = null
            get() {
                if (field == null) {
                    field = AES()
                }
                return field
            }
            private set

        private fun hexToBytes(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((((s[i].digitToIntOrNull(16)
                    ?: (-1 shl 4)) + s[i + 1].digitToIntOrNull(16)!!) ?: -1)).toByte()
                i += 2
            }
            return data
        }
    }
}