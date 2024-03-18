package com.muedsa.uitl

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun ByteArray.encryptAES128CBCPKCS7(key: String, iv: String): ByteArray {
    val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
    val ivParameterSpec = IvParameterSpec(iv.toByteArray())
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
    return cipher.doFinal(this)
}

fun ByteArray.decryptAES128CBCPKCS7(key: String, iv: String): ByteArray {
    val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
    val ivParameterSpec = IvParameterSpec(iv.toByteArray())
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
    return cipher.doFinal(this)
}

fun String.encryptAES128CBCPKCS7(key: String, iv: String): ByteArray {
    return this.toByteArray(Charsets.UTF_8).encryptAES128CBCPKCS7(key, iv)
}

fun String.decryptAES128CBCPKCS7(key: String, iv: String): ByteArray {
    return this.toByteArray(Charsets.UTF_8).decryptAES128CBCPKCS7(key, iv)
}

fun String.md5(): ByteArray {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(this.toByteArray())
}

@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.encodeBase64() = Base64.encode(this)

@OptIn(ExperimentalEncodingApi::class)
fun String.decodeBase64() = Base64.decode(this)

@OptIn(ExperimentalEncodingApi::class)
fun String.decodeBase64ToStr() = Base64.decode(this).toString(Charsets.UTF_8)