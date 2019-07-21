package com.ayteneve93.apexexplorer.view.base

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

abstract class BaseViewModel<N>(application : Application) : AndroidViewModel(application) {

    private var mNavigator : WeakReference<N>? = null
    private val mCompositeDisposable = CompositeDisposable()
    val mIsOnProgress = ObservableField<Boolean>(false)

    override fun onCleared() {
        mCompositeDisposable.dispose()
        super.onCleared()
    }

    fun addCompositeDisposable(disposable : Disposable) {
        mCompositeDisposable.add(disposable)
    }

    fun setNavigator(navigator : N) {
        this.mNavigator = WeakReference(navigator)
    }

    fun getNavigator() : N? {
        return mNavigator?.get()
    }

}