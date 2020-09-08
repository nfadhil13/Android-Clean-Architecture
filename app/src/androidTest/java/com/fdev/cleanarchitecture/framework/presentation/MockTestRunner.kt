package com.fdev.cleanarchitecture.framework.presentation

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import kotlinx.coroutines.FlowPreview

class MockTestRunner : AndroidJUnitRunner(){

    @FlowPreview
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestBaseApplication::class.java.name, context)
    }
}