package com.lihb.network.upload

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*


class ProgressResponseBody(
    val responseBody: ResponseBody,
    val progressListener: ProgressResponseListener?
) : ResponseBody() {
//    //实际的待包装响应体
//    private val responseBody: ResponseBody? = null
//
//    //进度回调接口
//    private val progressListener: ProgressResponseListener? = null

    //包装完成的BufferedSource
    private var bufferedSource: BufferedSource? = null

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    /**
     * 重写进行包装source
     *
     * @return BufferedSource
     */
    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            //包装
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            //当前读取字节数
            var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                //回调，如果contentLength()不知道长度，会返回-1
                progressListener?.onResponseProgress(
                    totalBytesRead,
                    responseBody.contentLength(),
                    bytesRead == -1L
                )
                return bytesRead
            }
        }
    }
}