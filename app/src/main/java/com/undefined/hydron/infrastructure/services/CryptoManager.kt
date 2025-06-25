package com.undefined.hydron.infrastructure.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import androidx.core.content.edit

class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyAlias = "conab"
    private val keySize = 256
    private val ivSize = 12
    private val gcmTagLength = 128
    private val sharedPreferencesName = "crypto_shared_prefs"
    private val sharedPreferencesKey = "encrypted_key_$keyAlias"

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keySize, SecureRandom())
        return keyGenerator.generateKey()
    }

    private fun getOrCreateKey(): SecretKey {
        val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        val encryptedKeyBase64 = sharedPreferences.getString(sharedPreferencesKey, null)
        return if (encryptedKeyBase64 == null) {
            val key = generateKey()
            val encryptedKey = key.encoded
            val encryptedKeyString = android.util.Base64.encodeToString(encryptedKey, android.util.Base64.DEFAULT)
            sharedPreferences.edit() { putString(sharedPreferencesKey, encryptedKeyString) }
            key
        } else {
            val encryptedKey = android.util.Base64.decode(encryptedKeyBase64, android.util.Base64.DEFAULT)
            SecretKeySpec(encryptedKey, "AES")
        }
    }

    fun encrypt(data: String): ByteArray {
        val secretKey = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(ivSize)
        SecureRandom().nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(gcmTagLength, iv))
        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return iv + encryptedData
    }

    fun decrypt(encryptedData: ByteArray): String {
        val secretKey = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = encryptedData.copyOfRange(0, ivSize)
        val encryptedDataWithoutIv = encryptedData.copyOfRange(ivSize, encryptedData.size)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(gcmTagLength, iv))
        val decryptedData = cipher.doFinal(encryptedDataWithoutIv)
        return String(decryptedData, Charsets.UTF_8)
    }
}