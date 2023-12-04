package com.lihb.network.upload

/**
 * 请求体进度回调接口，用于文件上传进度回调
 */
interface ProgressRequestListener {
    fun onRequestProgress(bytesWritten: Long, contentLength: Long, done: Boolean)
}

/**
 * 响应体进度回调接口，用于文件下载进度回调
 */
interface ProgressResponseListener {
    fun onResponseProgress(bytesRead: Long, contentLength: Long, done: Boolean)
}