package dev.superboring.aosp.chakonati.signal

import androidx.room.withTransaction
import dev.superboring.aosp.chakonati.extras.msgpack.serialized
import dev.superboring.aosp.chakonati.persistence.dao.generateNewKeyId
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.LocalPreKey
import dev.superboring.aosp.chakonati.protocol.requests.keyexchange.OneTimePreKey
import dev.superboring.aosp.chakonati.services.KeyExchange
import org.whispersystems.libsignal.ecc.Curve
import org.whispersystems.libsignal.state.PreKeyRecord

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
                    PersistentProtocolStore.storePreKey(preKeyId, PreKeyRecord(preKeyId, preKeyPair))
                }

                KeyExchange.publishOneTimePreKeys(preKeys)
            }
        }
    }

}
