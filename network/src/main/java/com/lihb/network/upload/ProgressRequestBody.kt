package com.lihb.network.upload

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import java.io.IOException


class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val progressListener: ProgressRequestListener?
) : RequestBody() {

    companion object {
        fun create(
            requestBody: RequestBody,
            progressFlowOnSubscribe: ProgressFlowOnSubscribe
        ): ProgressRequestBody {
            return ProgressRequestBody(requestBody, object : ProgressRequestListener {
                override fun onRequestProgress(
                    bytesWritten: Long,
                    contentLength: Long,
                    done: Boolean
                ) {
                    progressFlowOnSubscribe.onProgress(bytesWritten, contentLength, done)
                }
            })
        }
    }
//    //实际的待包装请求体
//    private var requestBody: RequestBody? = null
//
//    //进度回调接口
//    private var progressListener: ProgressRequestListener? = null

    //包装完成的BufferedSink
//    private var bufferedSink: BufferedSink? = null


    /**
     * 重写调用实际的响应体的contentType
     * @return MediaType
     */
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     * @return contentLength
     * @throws IOException 异常
     */
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    /**
     * 重写进行写入
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    override fun writeTo(sink: BufferedSink) {
//        if (bufferedSink == null) {
////            //包装
//            bufferedSink = sink(sink).buffer()
//        }
        //写入
        requestBody.writeTo(sink)
        //必须调用flush，否则最后一部分数据可能不会被写入
//        bufferedSink?.flush()
    }

    /**
     * 写入，回调进度接口
     * @param sink Sink
     * @return Sink
     */
    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten = 0L

            //总字节长度，避免多次调用contentLength()方法
            var contentLength = 0L

            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                //回调
                progressListener?.onRequestProgress(
                    bytesWritten,
                    contentLength,
                    bytesWritten == contentLength
                )

            }
        }
    }
}