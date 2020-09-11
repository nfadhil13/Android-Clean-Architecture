package com.fdev.cleanarchitecture.framework.presentation

import com.fdev.cleanarchitecture.business.domain.state.DialogInputCaptureCallback
import com.fdev.cleanarchitecture.business.domain.state.Response
import com.fdev.cleanarchitecture.business.domain.state.StateMessageCallback


interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}