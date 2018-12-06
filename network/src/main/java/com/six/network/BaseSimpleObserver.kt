package com.six.network

import android.content.Context
import com.six.baseblock.util.LoadingDialogUtil

abstract class BaseSimpleObserver<T>
@JvmOverloads constructor(context: Context, isShowLoading: Boolean = true, loadingMsg: CharSequence? = context.getString(R.string.loading))
    : BaseObserver<T, T>(context, isShowLoading, loadingMsg) {

    final override fun getSuccessData(t: T): T {
        return t
    }

    override fun showLoading(context: Context, loadingMsg: CharSequence?) {
        super.showLoading(context, loadingMsg)
        LoadingDialogUtil.show(context, loadingMsg)
    }

    override fun dismissLoading(context: Context) {
        super.dismissLoading(context)
        LoadingDialogUtil.dismiss()
    }

    override fun onFail(t: T) {
    }
}