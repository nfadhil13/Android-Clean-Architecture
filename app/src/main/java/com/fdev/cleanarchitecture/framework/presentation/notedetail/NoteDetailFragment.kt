package com.fdev.cleanarchitecture.framework.presentation.notedetail

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fdev.cleanarchitecture.business.domain.util.DateUtil
import com.fdev.cleanarchitecture.framework.presentation.common.BaseNoteFragment


class NoteDetailFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseNoteFragment() {

    val viewModel : NoteDetailViewModel by viewModels{
        viewModelFactory
    }

    override fun inject() {
        TODO("Not yet implemented")
    }

}