package com.fdev.cleanarchitecture.framework

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.fdev.cleanarchitecture.framework.presentation.TestBaseApplication
import kotlinx.coroutines.FlowPreview


abstract class BaseTest {

    // dependencies
    @FlowPreview
    val application: TestBaseApplication
            = ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    abstract fun injectTest()
}