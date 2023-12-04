package com.lihb.network.single

abstract class BaseSimpleSingleObserver<T : Any>
@JvmOverloads constructor(
    isShowLoading: Boolean = true,
    loadingMsg: CharSequence? = null
) : BaseSingleObserver<T, T>(isShowLoading, loadingMsg) {

    override fun getSuccessData(t: T): T {
        return t
    }

    override fun onFail(t: T) {
    }
}