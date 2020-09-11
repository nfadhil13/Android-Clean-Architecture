package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.interactors.common.DeleteNote
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState

class NoteListInteractors(
    val deleteMultipleNotes: DeleteMultipleNotes,
    val getNumberOfNotes: GetNumberOfNotes,
    val insertNewNote: InsertNewNote,
    val restoreDeletedNote: RestoreDeletedNote,
    val searchNotes: SearchNote,
    val deleteNote : DeleteNote<NoteListViewState>
)