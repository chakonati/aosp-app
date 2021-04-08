package dev.superboring.aosp.chakonati.protocol

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class Response

open class EmptyResponse : Response()
