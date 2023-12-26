package com.lihb.network

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import autodispose2.SingleSubscribeProxy
import autodispose2.androidx.lifecycle.autoDispose
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * 对 Observable<T>的扩展
 */
//
//fun <T : Any> SingleSubscribeProxy<T>.dontCareResult(
//    showLoading: Boolean = true,
//    loadingMsg: CharSequence? = "加载中",
//    success: ((T?) -> Unit)? = null
//) {
//    this.subscribe(object :
//        DefDontCareSingleObserver<T>(showLoading, loadingMsg) {
//        override fun onSuccessful(data: T?) {
//            success?.invoke(data)
//        }
//    })
//}
//
//fun <T : Any> SingleSubscribeProxy<T>.subscribeViewLoading(
//    loading: View?,
//    showLoading: Boolean = true,
//    success: ((T?) -> Unit)?
//) {
//    this.subscribe(object : ViewLoadingSingleObserver<T>(loading, showLoading) {
//        override fun onSuccessful(data: T?) {
//            success?.invoke(data)
//        }
//    })
//}
//
//fun <T : Any> SingleSubscribeProxy<T>.subscribeViewLoading(
//    loading: View?,
//    showLoading: Boolean = true,
//    success: ((T?) -> Unit)?,
//    fail: ((T) -> Boolean)? = null,
//    exception: ((reason: RequestExceptionReason, e: Throwable) -> Boolean)? = null,
//    end: ((Boolean, T?) -> Unit)? = null
//) {
//    this.subscribe(object : ViewLoadingSingleObserver<T>(loading, showLoading) {
//        override fun onSuccessful(data: T?) {
//            success?.invoke(data)
//        }
//
//        override fun onException(reason: RequestExceptionReason, e: Throwable) {
//            super.onException(reason, e)
//            exception?.invoke(reason, e)
//        }
//
//        override fun onFail(t: T) {
//            if (fail?.invoke(t) != true) {
//                super.onFail(t)
//            }
//        }
//
//        override fun onEnd(success: Boolean, t: T?) {
//            super.onEnd(success, t)
//            end?.invoke(success, t)
//        }
//
//    })
//}
//
////
//fun <T : Any> SingleSubscribeProxy<T>.subscribeDef(
//    showLoading: Boolean = true,
//    loadingMsg: CharSequence? = "加载中",
//    success: ((T?) -> Unit)?
//) {
//    this.subscribe(object : DefSingleObserver<T>(showLoading, loadingMsg) {
//        override fun onSuccessful(data: T?) {
//            success?.invoke(data)
//        }
//
//    })
//}
//
//
//fun <T : Any> SingleSubscribeProxy<T>.subscribeDef(
//    showLoading: Boolean = true,
//    loadingMsg: CharSequence? = "加载中",
//    success: ((T?) -> Unit)?,
//    fail: ((T?) -> Boolean)? = null,
//    exception: ((reason: RequestExceptionReason, e: Throwable) -> Boolean)? = null,
//    end: ((Boolean, T?) -> Unit)? = null
//) {
//    this.subscribe(object : DefSingleObserver<T>(showLoading, loadingMsg) {
//        override fun onSuccessful(data: T?) {
//            success?.invoke(data)
//        }
//
//        override fun onException(reason: RequestExceptionReason, e: Throwable) {
//            if (exception?.invoke(reason, e) != true) {
//                super.onException(reason, e)
//            }
//        }
//
//        override fun onFail(t: T) {
//            if (fail?.invoke(t) != true) {
//                super.onFail(t)
//            }
//        }
//
//        override fun onEnd(success: Boolean, t: T?) {
//            super.onEnd(success, t)
//            end?.invoke(success, t)
//        }
//
//    })
//}

fun <T : Any> Single<T>.observeOnMain(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T : Any> Single<T>.subscribeOnIO(): Single<T> {
    return this.subscribeOn(Schedulers.io())
}

fun <T : Any> Single<T>.toggleThread(): Single<T> {
    return this.observeOnMain().subscribeOnIO()
}

@Deprecated(
    "Deprecated! Because it has the same function name as [autodispose2.androidx.lifecycle.KotlinExtensionsKt.autoDispose] ",
    replaceWith = ReplaceWith("toggleDispose"),
    DeprecationLevel.ERROR
)
fun <T : Any> Single<T>.autoDispose(
    owner: LifecycleOwner,
    toggleThread: Boolean = true,
    untilEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
): SingleSubscribeProxy<T> {
    return toggleDispose(owner, toggleThread, untilEvent)
}

fun <T : Any> Single<T>.toggleDispose(
    owner: LifecycleOwner,
    toggleThread: Boolean = true,
    untilEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY
): SingleSubscribeProxy<T> {
    return if (toggleThread) {
        this.toggleThread()
    } else {
        this
    }.autoDispose(owner, untilEvent)
}

