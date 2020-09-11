package com.fdev.cleanarchitecture.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.interactors.notedetail.NoteDetailInteractors
import com.fdev.cleanarchitecture.business.interactors.notelist.NoteListInteractors
import com.fdev.cleanarchitecture.framework.presentation.common.NoteViewModelFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object NoteViewModelModule {

    @Singleton
    @JvmStatic
    @Provides
    fun provideNoteViewModelFactory(
        noteListInteractors: NoteListInteractors,
        noteDetailInteractors: NoteDetailInteractors,
        noteFactory: NoteFactory,
        editor: SharedPreferences.Editor,
        sharedPreferences: SharedPreferences
    ): ViewModelProvider.Factory{
        return NoteViewModelFactory(
            noteListInteractors = noteListInteractors,
            noteDetailInteractors = noteDetailInteractors,
            noteFactory = noteFactory,
            editor = editor,
            sharedPreferences = sharedPreferences
        )
    }

}