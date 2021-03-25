package dev.superboring.aosp.chakonati.signal

import androidx.room.withTransaction
import dev.superboring.aosp.chakonati.persistence.dao.generateNewKeyId
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.LocalPreKey
import dev.superboring.aosp.chakonati.protocol.requests.keyexchange.OneTimePreKey
import dev.superboring.aosp.chakonati.services.KeyExchange
import org.whispersystems.libsignal.ecc.Curve

object OneTimePreKeyRefresh {

    const val minKeys = 40
    const val maxKeys = 100

    suspend fun refreshOneTimePreKeys() {
        val keyCount = db.localPreKeys().count()
        if (keyCount < minKeys) {
            db.withTransaction {
                val preKeys = ArrayList<OneTimePreKey>(maxKeys - keyCount)
                for (i in keyCount..maxKeys) {
                    val preKeyPair = Curve.generateKeyPair()
                    val preKeyId = db.localPreKeys().generateNewKeyId()
                    preKeys.add(OneTimePreKey(preKeyId, preKeyPair.publicKey.serialize()))
                    db.localPreKeys() insert LocalPreKey(
                        preKeyId,
                        preKeyPair.publicKey.serialize(),
                        preKeyPair.privateKey.serialize()
                    )
                }
                
                KeyExchange.publishOneTimePreKeys(preKeys)
            }
        }
    }

}
