package com.six.network

import android.content.Context
import java.lang.ref.WeakReference

class SilentObserver @JvmOverloads constructor(
    context: WeakReference<Context>?,
    isShowLoading: Boolean = false,
    loadingMsg: CharSequence? = null
) : BaseSimpleObserver<Any>(context, isShowLoading, loadingMsg) {
    override fun showLoading(context: WeakReference<Context>?, loadingMsg: CharSequence?) {

    }

    override fun dismissLoading(context: WeakReference<Context>?) {

    }

    override fun isSuccess(t: Any): Boolean = true

    override fun onSuccess(data: Any?) {}
}