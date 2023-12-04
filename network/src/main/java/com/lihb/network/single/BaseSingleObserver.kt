package com.lihb.network.single

import android.net.ParseException
import androidx.annotation.CallSuper
import com.google.gson.JsonParseException
import com.lihb.network.RequestExceptionReason
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException


abstract class BaseSingleObserver<T1 : Any, T2 : Any?>
@JvmOverloads constructor(
    private val isShowLoading: Boolean = true,
    private val loadingMsg: CharSequence? = null
) : SingleObserver<T1> {

    /**
     * 显示loading，重写实现自己的逻辑
     */
    abstract fun showLoading(loadingMsg: CharSequence?)

    /**
     * 隐藏loading，重写实现自己的逻辑
     */
    abstract fun dismissLoading()

    protected abstract fun getSuccessData(t: T1): T2?

    /**
     * 是否成功，重现实现自己的判断逻辑
     */
    protected abstract fun isSuccess(t: T1): Boolean

    /**
     * 成功时回调
     */
    protected abstract fun onSuccessful(data: T2?)

    /**
     * 失败时回调
     */
    protected abstract fun onFail(t: T1)

    /**
     * 结束时回调，无论成功失败还是错误
     */
    protected open fun onEnd(success: Boolean, t: T1?) {

    }

    /**
     * 错误时回调
     */
    final override fun onError(e: Throwable) {
        when (e) {
            is HttpException -> {     //   HTTP错误
                onException(RequestExceptionReason.BAD_SERVICE, e)
            }

            is ConnectException, is UnknownHostException -> {   //   连接错误
                onException(RequestExceptionReason.CONNECT_ERROR, e)
            }

            is InterruptedIOException -> {   //  连接超时
                onException(RequestExceptionReason.CONNECT_TIMEOUT, e)
            }

            is JsonParseException, is JSONException, is ParseException -> {   //  解析错误
                onException(RequestExceptionReason.PARSE_ERROR, e)
            }

            else -> {
                onException(RequestExceptionReason.UNKNOWN_ERROR, e)
            }
        }
        onEnd(false, null)
        dismissLoading()
    }

    @CallSuper
    open fun onException(reason: RequestExceptionReason, e: Throwable) {
        e.printStackTrace()
//        if (App.debug() && e is HttpException) {
//            //debug而且是httpException的时候才toast具体内容
//            try {
//                ToastUtil.show(
//                    "${e.response()?.toString()}\n${e.response()?.errorBody()?.string()}",
//                    Toast.LENGTH_LONG
//                )
//            } catch (e1: Exception) {
//                e1.printStackTrace()
//                onException(RequestExceptionReason.BAD_NETWORK, e)
//            }
//
//        } else {
//            LogHelper.sv("onException:" + App.context<Application>().getString(reason.msg))
//            ToastUtil.show(reason.msg)
//        }
    }

    @CallSuper
    override fun onSubscribe(d: Disposable) {
        if (isShowLoading) {
            this.showLoading(loadingMsg)
        }
    }

    final override fun onSuccess(t: T1) {
        if (isSuccess(t)) {
            onSuccessful(getSuccessData(t))
            onEnd(true, t)
        } else {
            onFail(t)
            onEnd(false, t)
        }
        dismissLoading()
    }
}