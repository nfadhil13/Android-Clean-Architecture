package com.fdev.cleanarchitecture.framework.presentation.notedetail


import com.fdev.cleanarchitecture.business.domain.state.StateEvent
import com.fdev.cleanarchitecture.business.interactors.notedetail.NoteDetailInteractors
import com.fdev.cleanarchitecture.framework.presentation.common.BaseViewModel
import com.fdev.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


const val NOTE_DETAIL_ERROR_RETRIEVEING_SELECTED_NOTE = "Error retrieving selected note from bundle."
const val NOTE_DETAIL_SELECTED_NOTE_BUNDLE_KEY = "selectedNote"
const val NOTE_TITLE_CANNOT_BE_EMPTY = "Note title can not be empty."


@ExperimentalCoroutinesApi
@FlowPreview
class NoteDetailViewModel
@Inject
constructor(
    private val noteDetailInteractors: NoteDetailInteractors
): BaseViewModel<NoteDetailViewState>(){

    override fun handleNewData(data: NoteDetailViewState) {

    }

    override fun setStateEvent(stateEvent: StateEvent) {

    }

    override fun initNewViewState(): NoteDetailViewState {
        return NoteDetailViewState()
    }

}