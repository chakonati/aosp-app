package dev.superboring.aosp.chakonati.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.persistence.entities.MySetup
import dev.superboring.aosp.chakonati.service.prepareOwnRelayCommunicator

@Dao
interface MySetupDao : SingleEntryDao<MySetup> {

    override val defaultValue
        get() = MySetup(
            identityPrivateKey = byteArrayOf(),
            identityPublicKey = byteArrayOf(),
            registrationId = -1,
            relayServer = "",
            isSetUp = false,
        )

    @Query("select * from my_setup limit 1")
    override fun getValue(): MySetup

    @Query("select count(id) as count from my_setup")
    override fun count(): Int

    @Insert
    override infix fun insert(element: MySetup)

    @Update
    override infix fun update(element: MySetup)
}

fun MySetupDao.saveRelayServer(relayServer: String) {
    db.mySetup().get().apply {
        this.relayServer = relayServer
    }.save()
    prepareOwnRelayCommunicator()
}
fun MySetup.save() = db.mySetup() save this
