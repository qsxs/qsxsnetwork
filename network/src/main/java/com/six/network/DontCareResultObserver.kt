package com.six.network

import android.content.Context
import java.lang.ref.WeakReference

/**
 * 不关心任何返回结果
 */
@Deprecated("弃用", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("SilentObserver"))
abstract class DontCareResultObserver @JvmOverloads constructor(
    context: WeakReference<Context>?,
    isShowLoading: Boolean = true,
    loadingMsg: CharSequence? = null
) : BaseSimpleObserver<Any>(context, isShowLoading, loadingMsg) {

    override fun isSuccess(t: Any): Boolean {
        return true
    }

    override fun onSuccess(data: Any?) {

    }
}