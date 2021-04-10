package dev.superboring.aosp.chakonati.protocol

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Notification(
    val subscriptionName: String,
    val data: ByteArray,
)


