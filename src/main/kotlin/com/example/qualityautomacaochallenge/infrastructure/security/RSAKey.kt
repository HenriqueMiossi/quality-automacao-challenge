package com.example.qualityautomacaochallenge.infrastructure.security

import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

@Service
class RSAKey {
    fun getPublicKey(): PublicKey {
        val key = readKeyFile("./public.crt")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")

        val keySpec = X509EncodedKeySpec(Base64.decodeBase64(key))
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(keySpec)
    }

    fun getPrivateKey(): PrivateKey {
        val key = readKeyFile("./private.pem")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")

        val keySpec = PKCS8EncodedKeySpec(Base64.decodeBase64(key))
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePrivate(keySpec)
    }

    private fun readKeyFile(path: String): String {
        val file = File(path)
        return String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())
    }
}