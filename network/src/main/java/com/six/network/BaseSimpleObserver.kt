package com.six.network

import android.content.Context
import java.lang.ref.WeakReference

abstract class BaseSimpleObserver<T>
@JvmOverloads constructor(
    context: WeakReference<Context>?,
    isShowLoading: Boolean = true,
    loadingMsg: CharSequence? = null
) : BaseObserver<T, T>(context, isShowLoading, loadingMsg) {

    final override fun getSuccessData(t: T): T {
        return t
    }

    override fun onFail(t: T) {
    }
}