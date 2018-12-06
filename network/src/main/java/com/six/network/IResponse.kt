package com.six.network
 interface IResponse {
    fun isSuccess(): Boolean

    fun getErrorMessage(): String
}
