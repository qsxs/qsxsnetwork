package com.lihb.network.single

import android.view.View
import java.lang.ref.WeakReference

/**
 * rxjava常用的网络请求观察类，使用占位 view 代替 loading dialog
 * @param loading 要操作的 view
 * @param bringToFront 是否需要 bringToFront
 */
abstract class BaseViewLoadingSingleObserver<T : Any> @JvmOverloads constructor(
    loading: View?,
    private val bringToFront: Boolean = false
) : BaseSimpleSingleObserver<T>(true, null) {
    private var weakLoadingView: WeakReference<View?> = WeakReference(loading)

    override fun showLoading(loadingMsg: CharSequence?) {
        weakLoadingView.get()?.apply {
            visibility = View.VISIBLE
            if (bringToFront) bringToFront()
        }
    }

    override fun dismissLoading() {
        weakLoadingView.get()?.visibility = View.GONE
    }
}