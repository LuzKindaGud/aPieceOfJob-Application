package com.bsit.peerx.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyStoreManager @Inject constructor() {

    companion object {
        private const val KEY_ALIAS = "PeerX_identity_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }

    /**
     * Retrieves the existing identity key pair or generates a new one if it doesn't exist.
     */
    fun getOrCreateKeyPair(): KeyPair {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val privateKey = keyStore.getKey(KEY_ALIAS, null) as PrivateKey
            val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey
            return KeyPair(publicKey, privateKey)
        }

        val kpg = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            ANDROID_KEYSTORE
        )

        val parameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            setDigests(KeyProperties.DIGEST_SHA256)
            setUserAuthenticationRequired(false)
            build()
        }

        kpg.initialize(parameterSpec)
        return kpg.generateKeyPair()
    }

    /**
     * Returns the raw bytes of the public key.
     */
    fun getPublicKeyBytes(): ByteArray {
        return getOrCreateKeyPair().public.encoded
    }

    /**
     * Returns a stable Peer ID based on the SHA-256 hash of the public key.
     */
    fun getPeerIdHash(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(getPublicKeyBytes())
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    /**
     * Signs the provided data using the identity private key.
     */
    fun sign(data: ByteArray): ByteArray {
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(getOrCreateKeyPair().private)
        signature.update(data)
        return signature.sign()
    }

    /**
     * Verifies a signature against a public key and data.
     */
    fun verify(publicKeyBytes: ByteArray, data: ByteArray, signature: ByteArray): Boolean {
        return try {
            val kf = KeyFactory.getInstance("EC")
            val publicKey = kf.generatePublic(X509EncodedKeySpec(publicKeyBytes))
            val sig = Signature.getInstance("SHA256withECDSA")
            sig.initVerify(publicKey)
            sig.update(data)
            sig.verify(signature)
        } catch (e: Exception) {
            false
        }
    }
}



