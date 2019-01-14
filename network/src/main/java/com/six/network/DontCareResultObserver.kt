package com.six.network

import android.content.Context

/**
 * 不关心任何返回结果
 */
abstract class DontCareResultObserver @JvmOverloads constructor(
    context: Context,
    showLoading: Boolean = false,
    loadingMsg: CharSequence? = context.getString(R.string.loading)
) : BaseSimpleObserver<Any>(context, showLoading, loadingMsg) {

    override fun isSuccess(t: Any): Boolean {
        return true
    }

    override fun onSuccess(data: Any?) {

    }
}