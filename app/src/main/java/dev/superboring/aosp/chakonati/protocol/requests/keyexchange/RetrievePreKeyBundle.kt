package dev.superboring.aosp.chakonati.protocol.requests.keyexchange

import dev.superboring.aosp.chakonati.protocol.*
import org.msgpack.core.MessageUnpacker
import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.ecc.ECPublicKey
import org.whispersystems.libsignal.state.PreKeyBundle

class RetrievePreKeyBundleRequest :
    Request<RetrievePreKeyBundleResponse>("KeyExchange.preKeyBundle") {
    override fun newResponse(): RetrievePreKeyBundleResponse {
        return RetrievePreKeyBundleResponse()
    }
}

class RetrievePreKeyBundleResponse : Response(9) {
    lateinit var bundle: PreKeyBundle
    var error: Error = null

    override fun unpack(unpacker: MessageUnpacker) = unpacker.run {
        bundle = PreKeyBundle(
            unpackInt(),
            unpackInt(),
            unpackInt(),
            ECPublicKey.fromPublicKeyBytes(unpackByteArray()),
            unpackInt(),
            ECPublicKey.fromPublicKeyBytes(unpackByteArray()),
            unpackByteArray(),
            IdentityKey(unpackByteArray())
        )
        error = unpackError()
    }
}