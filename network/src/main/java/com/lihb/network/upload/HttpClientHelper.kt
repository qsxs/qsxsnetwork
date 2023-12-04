package com.lihb.network.upload

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


//var file: File = File(fileUri)
//var requestFile: RequestBody = create(MediaType.parse("multipart/form-data"), file)
//
////将requestFile封装成ProgressRequestBody传入
//var body: Part = createFormData.createFormData(
//    "file",
//    file.getName(),
//    ProgressRequestBody(requestFile, this)
//) //this是在当前类实现了ProgressRequestListener接口
//
//var call: Call<ResponseBody> = service.upload(body)

object HttpClientHelper {
    /**
     * 包装OkHttpClient，用于下载文件的回调
     * @param progressListener 进度回调接口
     * @return 包装后的OkHttpClient
     */
    fun addProgressResponseListener(progressListener: ProgressResponseListener?): OkHttpClient {
        val client = OkHttpClient.Builder()
        //增加拦截器
        client.addInterceptor(Interceptor { chain -> //拦截
            val originalResponse: Response = chain.proceed(chain.request())
            //包装响应体并返回
            originalResponse.newBuilder()
                .body(ProgressResponseBody(originalResponse.body!!, progressListener))
                .build()
        })
        return client.build()
    }

    /**
     * 包装OkHttpClient，用于上传文件的回调
     * @param progressListener 进度回调接口
     * @return 包装后的OkHttpClient
     */
    fun addProgressRequestListener(progressListener: ProgressRequestListener?): OkHttpClient {
        val client = OkHttpClient.Builder()
        //增加拦截器
        client.addInterceptor(Interceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .method(
                    original.method,
                    ProgressRequestBody(original.body!!, progressListener)
                )
                .build()
            chain.proceed(request)
        })
        return client.build()
    }
}