package com.fdev.cleanarchitecture.framework.presentation.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.interactors.notedetail.NoteDetailInteractors
import com.fdev.cleanarchitecture.business.interactors.notelist.NoteListInteractors
import com.fdev.cleanarchitecture.framework.presentation.notedetail.NoteDetailViewModel
import com.fdev.cleanarchitecture.framework.presentation.notelist.viewmodel.NoteListViewModel
import com.fdev.cleanarchitecture.framework.presentation.splash.SplashViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class NoteViewModelFactory
@Inject
constructor(
    private val noteListInteractors: NoteListInteractors,
    private val noteDetailInteractors: NoteDetailInteractors,
    private val noteFactory: NoteFactory,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){

            NoteListViewModel::class.java -> {
                NoteListViewModel(
                    noteListInteractors = noteListInteractors,
                    noteFactory = noteFactory,
                    editor = editor,
                    sharedPreferences = sharedPreferences
                ) as T
            }

            NoteDetailViewModel::class.java -> {
                NoteDetailViewModel(
                    noteDetailInteractors = noteDetailInteractors
                ) as T
            }

            SplashViewModel::class.java -> {
                SplashViewModel() as T
            }

            else -> {
                throw IllegalArgumentException("unknown model class $modelClass")
            }
        }
    }
}
