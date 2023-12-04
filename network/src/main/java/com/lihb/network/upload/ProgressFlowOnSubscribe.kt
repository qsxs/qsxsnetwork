package com.lihb.network.upload

import io.reactivex.rxjava3.core.FlowableEmitter
import io.reactivex.rxjava3.core.FlowableOnSubscribe

class ProgressFlowOnSubscribe : FlowableOnSubscribe<Any> {
    var emitter: FlowableEmitter<Any>? = null
//    override fun subscribe(emitter: FlowableEmitter<Double>) {
//        this.emitter = emitter
//    }

    fun onProgress(bytesWritten: Long, contentLength: Long, done: Boolean) {
        emitter?.onNext(bytesWritten.toDouble().div(contentLength.toDouble()))
    }

    override fun subscribe(emitter: FlowableEmitter<Any>) {
        this.emitter = emitter
    }
}