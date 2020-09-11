package com.fdev.cleanarchitecture.framework.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fdev.cleanarchitecture.R
import com.fdev.cleanarchitecture.business.domain.util.DateUtil
import com.fdev.cleanarchitecture.framework.presentation.common.BaseNoteFragment

class SplashFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
) : BaseNoteFragment() {


    val viewModel : SplashViewModel by viewModels{
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun inject() {

    }

}