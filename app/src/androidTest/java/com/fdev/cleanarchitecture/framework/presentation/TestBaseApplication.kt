package com.fdev.cleanarchitecture.framework.presentation

import com.fdev.cleanarchitecture.di.DaggerTestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
class TestBaseApplication : BaseApplication() {

    @ExperimentalCoroutinesApi
    override fun initAppComponent() {
        appComponent = DaggerTestAppComponent.factory().create(this)
    }
}