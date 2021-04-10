package dev.superboring.aosp.chakonati.services

import dev.superboring.aosp.chakonati.protocol.exceptions.RequestFailure
import dev.superboring.aosp.chakonati.protocol.requests.subscriptions.SubscribeRequest
import dev.superboring.aosp.chakonati.service.OwnRelayServer

typealias SubscriptionName = String

object Subscriptions {

    const val MESSAGES = "messages"

}

object Subscription {

    suspend fun subscribe(subscriptionName: SubscriptionName) {
        OwnRelayServer.comm.send(SubscribeRequest(subscriptionName)).error?.let { error ->
            throw SubscribeFailed(subscriptionName, error)
        }
    }

}

class SubscribeFailed(subName: SubscriptionName, error: String) :
        RequestFailure("Failed to subscribe to $subName: $error")
