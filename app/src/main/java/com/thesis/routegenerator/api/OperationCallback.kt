package com.thesis.routegenerator.api

/**
 * Status response from server.
 */
interface OperationCallback {
    fun onSuccess(obj:Any?)
    fun onError(obj:Any?)
}