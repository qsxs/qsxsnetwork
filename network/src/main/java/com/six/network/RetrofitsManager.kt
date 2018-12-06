package com.six.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * 请求网络类
 * Created by lihuabin on 2017/12/4.
 */

class RetrofitsManager private constructor() {
    private val serviceMap = HashMap<Class<*>, Any>()
    private val retrofits = HashMap<String, Retrofit>()

    //  创建单例
    private object SingletonHolder {
        val INSTANCE = RetrofitsManager()
    }

    companion object {
        /**
         * 网络请求超时时间毫秒
         */
        private const val DEFAULT_TIMEOUT = 20000L

        @JvmOverloads
        fun <T> getApiService(baseUrl: String, clazz: Class<T>, interceptors: Array<Interceptor> = arrayOf(), certificates: Array<InputStream> = arrayOf()): T {
            var iApi: Any? = SingletonHolder.INSTANCE.serviceMap[clazz]
            if (iApi == null) {
                var retrofit: Retrofit? = SingletonHolder.INSTANCE.retrofits[baseUrl]
                if (retrofit == null) {

                    val builder = OkHttpClient.Builder()
                            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    for (interceptor in interceptors) {
                        builder.addInterceptor(interceptor)
                    }
                    //打印日志
                    val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                        Log.i(RetrofitsManager::class.java.simpleName, it)
                    })
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    builder.addInterceptor(interceptor)

                    //证书绑定
                    addCertificates(builder, certificates)

//                    if (sslSocketFactory != null && trustManager != null) {
//                        builder.sslSocketFactory(sslSocketFactory, trustManager)
//                    }

                    retrofit = Retrofit.Builder().baseUrl(baseUrl)
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build()
                    SingletonHolder.INSTANCE.retrofits[baseUrl] = retrofit
                }
                iApi = retrofit!!.create(clazz)
                SingletonHolder.INSTANCE.serviceMap[clazz] = iApi!!
            }
            return iApi as T
        }

        private fun addCertificates(builder: OkHttpClient.Builder, certificates: Array<InputStream>) {
            for (certificate in certificates) {
                var trustManager: X509TrustManager
                var sslSocketFactory: SSLSocketFactory
                val certificateFactory = CertificateFactory.getInstance("X.509")
                val cs = certificateFactory.generateCertificates(certificate)
                if (cs.isEmpty()) {
                    throw IllegalArgumentException("expected non-empty set of trusted certificates")
                } else {
                    //                    char[] password = "password".toCharArray(); // Any password will work.
                    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                    keyStore.load(null, null)

                    for ((index, cc) in cs.withIndex()) {
                        val certificateAlias = Integer.toString(index)
                        keyStore.setCertificateEntry(certificateAlias, cc)
                    }

                    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                    trustManagerFactory.init(keyStore)

                    val trustManagers = trustManagerFactory.trustManagers
                    if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
                        throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
                    }
                    trustManager = trustManagers[0] as X509TrustManager

                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())
                    //                    sslContext.init(null, new TrustManager[]{trustManager}, null);
                    sslSocketFactory = sslContext.socketFactory

                    builder.sslSocketFactory(sslSocketFactory, trustManager)
                }
            }
        }
    }
}
