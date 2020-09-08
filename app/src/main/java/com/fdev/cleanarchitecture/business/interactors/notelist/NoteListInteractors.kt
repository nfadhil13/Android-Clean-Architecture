package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.interactors.common.DeleteNote

class NoteListInteractors(
    private val deleteMultipleNotes: DeleteMultipleNotes,
    private val getNumberOfNotes: GetNumberOfNotes,
    private val insertNewNote: InsertNewNote,
    private val restoreDeletedNote: RestoreDeletedNote,
    private val searchNote: SearchNote,
    private val deleteNote : DeleteNote<NoteListInteractors>
)