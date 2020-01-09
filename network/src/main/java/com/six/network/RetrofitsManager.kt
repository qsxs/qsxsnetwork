package com.six.network

import com.six.network.config.RetrofitsConfig
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
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
        const val DEFAULT_TIMEOUT = 20000L

        @JvmOverloads
        fun <T> getApiService(
            baseUrl: String,
            clazz: Class<T>,
            client: OkHttpClient,
            converterFactory: Converter.Factory = GsonConverterFactory.create(),
            callAdapterFactory: CallAdapter.Factory = RxJava2CallAdapterFactory.create(),
            tag: String = ""
        ): T {
            var iApi: Any? = SingletonHolder.INSTANCE.serviceMap[clazz]
            if (iApi == null) {
                val key = "$baseUrl${clazz.simpleName}$tag"
                var retrofit: Retrofit? = SingletonHolder.INSTANCE.retrofits[key]
                if (retrofit == null) {
                    retrofit = Retrofit.Builder().baseUrl(baseUrl)
                        .client(client)
                        .addConverterFactory(converterFactory)
                        .addCallAdapterFactory(callAdapterFactory)
                        .build()
                    SingletonHolder.INSTANCE.retrofits[key] = retrofit
                }
                iApi = retrofit!!.create(clazz)
                SingletonHolder.INSTANCE.serviceMap[clazz] = iApi!!
            }
            return iApi as T
        }

        @JvmStatic
        fun <T> getApiService(config: RetrofitsConfig<T>): T {
            return getApiService(
                config.baseUrl,
                config.clazz,
                config.interceptors,
                config.networkInterceptors,
                config.certificatePinner,
                config.certificates,
                config.connectTimeout,
                config.writeTimeout,
                config.readTimeout,
                config.factories,
                config.tag
            )
        }

        @JvmOverloads
        @JvmStatic
        fun <T> getApiService(
            baseUrl: String,
            clazz: Class<T>,
            interceptors: Array<out Interceptor> = arrayOf(),
            networkInterceptors: Array<out Interceptor> = arrayOf(),
            certificatePinner: CertificatePinner? = null,
            certificates: Array<InputStream> = arrayOf(),
            connectTimeout: Long = DEFAULT_TIMEOUT,
            writeTimeout: Long = DEFAULT_TIMEOUT,
            readTimeout: Long = DEFAULT_TIMEOUT,
            factories: Array<Converter.Factory> = arrayOf(GsonConverterFactory.create()),
            tag: String = ""
        ): T {
            var iApi: Any? = SingletonHolder.INSTANCE.serviceMap[clazz]
            if (iApi == null) {
                val key = "$baseUrl${clazz.simpleName}$tag"
                var retrofit: Retrofit? =
                    SingletonHolder.INSTANCE.retrofits[key]
                if (retrofit == null) {

                    val builder = OkHttpClient.Builder()
                        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                        .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    if (certificatePinner != null) {
                        builder.certificatePinner(certificatePinner)
                    }
                    for (interceptor in interceptors) {
                        builder.addInterceptor(interceptor)
                    }
                    for (interceptor in networkInterceptors) {
                        builder.addNetworkInterceptor(interceptor)
                    }
//                    //打印日志
//                    val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
//                        Log.i(RetrofitsManager::class.java.simpleName, it)
//                    })
//                    interceptor.level = HttpLoggingInterceptor.Level.BODY
//                    builder.addInterceptor(interceptor)

                    //证书绑定
                    addCertificates(builder, certificates)

//                    if (sslSocketFactory != null && trustManager != null) {
//                        builder.sslSocketFactory(sslSocketFactory, trustManager)
//                    }

                    val retrofitBuild = Retrofit.Builder().baseUrl(baseUrl)
                        .client(builder.build())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    factories.forEach { factory ->
                        retrofitBuild.addConverterFactory(factory)
                    }

                    retrofit = retrofitBuild.build()
                    SingletonHolder.INSTANCE.retrofits[key] = retrofit
                }
                iApi = retrofit!!.create(clazz)
                SingletonHolder.INSTANCE.serviceMap[clazz] = iApi!!
            }
            @Suppress("UNCHECKED_CAST")
            return iApi as T
        }

        private fun addCertificates(
            builder: OkHttpClient.Builder,
            certificates: Array<InputStream>
        ) {
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

                    val trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                    trustManagerFactory.init(keyStore)

                    val trustManagers = trustManagerFactory.trustManagers
                    if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
                        throw IllegalStateException(
                            "Unexpected default trust managers:" + Arrays.toString(
                                trustManagers
                            )
                        )
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
