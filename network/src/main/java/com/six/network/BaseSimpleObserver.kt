package com.six.network

import android.content.Context

abstract class BaseSimpleObserver<T>
@JvmOverloads constructor(context: Context, isShowLoading: Boolean = true, loadingMsg: CharSequence? = context.getString(R.string.loading))
    : BaseObserver<T, T>(context, isShowLoading, loadingMsg) {

    final override fun getSuccessData(t: T): T {
        return t
    }

    override fun onFail(t: T) {
    }
}