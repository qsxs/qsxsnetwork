package com.six.network

import android.content.Context
import android.support.annotation.CallSuper
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 通用的Observer，T1原本接受的，T2 是经过 {#getSuccessData()}处理 {@link #onSuccess()}回调的
 */
abstract class BaseObserver<T1, T2>
@JvmOverloads constructor(
    private var context: Context,
    isShowLoading: Boolean = true,
    loadingMsg: CharSequence? = context.getString(R.string.loading)
) : Observer<T1> {

    init {
        if (isShowLoading) {
            showLoading(context, loadingMsg)
        }
    }


    /**
     * 显示loading，重写实现自己的逻辑
     */
    abstract fun showLoading(context: Context, loadingMsg: CharSequence?)

    /**
     * 隐藏loading，重写实现自己的逻辑
     */
    abstract fun dismissLoading(context: Context)

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
    @CallSuper
    protected open fun onEnd() {
        dismissLoading(context)
    }

    /**
     * 错误时回调
     */
    @CallSuper
    override fun onError(e: Throwable) {
        onEnd()
    }

    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {

    }

    final override fun onNext(t: T1) {
        if (isSuccess(t)) {
            onSuccess(getSuccessData(t))
        } else {
            onFail(t)
        }
        onEnd()
    }


}