package com.lihb.network

import androidx.annotation.StringRes

/**
 * 请求网络失败原因
 */
enum class RequestExceptionReason(@StringRes var msg: Int) {
    /**
     * 解析数据失败
     */
    PARSE_ERROR(R.string.error_response),

    /**
     * http错误，404之类的
     */
    BAD_SERVICE(R.string.server_exception),

    /**
     * 服务器返回了空
     */
    EMPTY_RESPONSE(R.string.empty_response),

    /**
     * 连接错误
     */
    CONNECT_ERROR(R.string.network_connection_fail),

    /**
     * 连接超时
     */
    CONNECT_TIMEOUT(R.string.connection_timed_out),

    /**
     * 未知错误
     */
    UNKNOWN_ERROR(R.string.unknown_exception);

}