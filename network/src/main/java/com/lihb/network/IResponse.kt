package com.lihb.network

interface IResponse {
    fun isSuccess(): Boolean

    fun getErrorMessage(): String
}
