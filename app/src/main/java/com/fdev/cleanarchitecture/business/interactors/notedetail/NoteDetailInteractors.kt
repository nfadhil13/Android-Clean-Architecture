package com.fdev.cleanarchitecture.business.interactors.notedetail

import com.fdev.cleanarchitecture.business.interactors.common.DeleteNote
import com.fdev.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailViewState

class NoteDetailInteractors(
    val deleteNote: DeleteNote<NoteDetailViewState>,
    val updateNote: UpdateNote
)