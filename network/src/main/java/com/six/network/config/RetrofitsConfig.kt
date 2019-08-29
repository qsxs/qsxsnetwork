package com.six.network.config

import com.six.network.RetrofitsManager.Companion.DEFAULT_TIMEOUT
import okhttp3.Interceptor
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

class RetrofitsConfig<T>(val baseUrl: String, val clazz: Class<T>) {
    var interceptors: Array<Interceptor> = arrayOf()
        private set
    var certificates: Array<InputStream> = arrayOf()
        private set
    var connectTimeout: Long = DEFAULT_TIMEOUT
        private set
    var writeTimeout: Long = DEFAULT_TIMEOUT
        private set
    var readTimeout: Long = DEFAULT_TIMEOUT
        private set
    var factories: Array<Converter.Factory> = arrayOf(GsonConverterFactory.create())
        private set
    var tag: String = ""
        private set

    fun setTag(tag: String): RetrofitsConfig<T> {
        this.tag = tag
        return this
    }

    fun setInterceptors(interceptor: Array<Interceptor>): RetrofitsConfig<T> {
        this.interceptors = interceptor
        return this
    }

    fun setCertificates(certificate: Array<InputStream>): RetrofitsConfig<T> {
        this.certificates = certificate
        return this
    }

    fun setConnectTimeout(timeInMillis: Long): RetrofitsConfig<T> {
        this.connectTimeout = timeInMillis
        return this
    }

    fun setWriteTimeout(timeInMillis: Long): RetrofitsConfig<T> {
        this.writeTimeout = timeInMillis
        return this
    }

    fun setReadTimeout(timeInMillis: Long): RetrofitsConfig<T> {
        this.readTimeout = timeInMillis
        return this
    }

    fun setTimeout(timeInMillis: Long): RetrofitsConfig<T> {
        this.connectTimeout = timeInMillis
        this.readTimeout = timeInMillis
        this.writeTimeout = timeInMillis
        return this
    }

    fun setConverterFactory(factories: Array<Converter.Factory>): RetrofitsConfig<T> {
        this.factories = factories
        return this
    }
}