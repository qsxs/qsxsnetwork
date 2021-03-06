package com.six.network

import android.content.Context
import androidx.annotation.CallSuper
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * 通用的Observer，T1原本接受的，T2 是经过 {#getSuccessData()}处理 {@link #onSuccess()}回调的
 */
abstract class BaseObserver<T1, T2>
@JvmOverloads constructor(
    protected var context: WeakReference<Context>?,
    isShowLoading: Boolean = true,
    loadingMsg: CharSequence? = null
) : Observer<T1> {

    init {
        if (isShowLoading) {
            this.showLoading(context, loadingMsg)
        }
    }


    /**
     * 显示loading，重写实现自己的逻辑
     */
    abstract fun showLoading(context: WeakReference<Context>?, loadingMsg: CharSequence?)

    /**
     * 隐藏loading，重写实现自己的逻辑
     */
    abstract fun dismissLoading(context: WeakReference<Context>?)

    protected abstract fun getSuccessData(t: T1): T2?

    /**
     * 是否成功，重现实现自己的判断逻辑
     */
    protected abstract fun isSuccess(t: T1): Boolean

    /**
     * 成功时回调
     */
    protected abstract fun onSuccess(data: T2?)

    /**
     * 失败时回调
     */
    protected abstract fun onFail(t: T1)

    /**
     * 结束时回调，无论成功失败还是错误
     */
    protected open fun onEnd(success:Boolean) {

    }

    /**
     * 错误时回调
     */
    @CallSuper
    override fun onError(e: Throwable) {
        onEnd(false)
        dismissLoading(context)
    }

    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {

    }

    final override fun onNext(t: T1) {
        if (isSuccess(t)) {
            onSuccess(getSuccessData(t))
            onEnd(true)
        } else {
            onFail(t)
            onEnd(false)
        }
        dismissLoading(context)

    }


}