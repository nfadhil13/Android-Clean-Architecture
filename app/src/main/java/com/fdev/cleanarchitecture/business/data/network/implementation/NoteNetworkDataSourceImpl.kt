package com.fdev.cleanarchitecture.business.data.network.implementation

import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.framework.datasource.network.abstraction.NoteFirestoreService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteNetworkDataSourceImpl
@Inject
constructor(
    private val noteFirestoreService: NoteFirestoreService
) : NoteNetworkDataSource {
    override suspend fun insertOrUpdateNote(note: Note) =
        noteFirestoreService.insertOrUpdateNote(note)

    override suspend fun deleteNote(primaryKey: String) =
        noteFirestoreService.deleteNote(primaryKey)

    override suspend fun insertDeletedNote(note: Note) =
        noteFirestoreService.insertDeletedNote(note)

    override suspend fun insertDeletedNotes(notes: List<Note>) =
        noteFirestoreService.insertDeletedNotes(notes)

    override suspend fun deleteDeletedNote(note: Note) =
        noteFirestoreService.deleteDeletedNote(note)

    override suspend fun getDeletedNote(): List<Note> =
        noteFirestoreService.getDeletedNote()

    override suspend fun deleteAllNotes()
            = noteFirestoreService.deleteAllNotes()

    override suspend fun searchNote(note: Note): Note? =
        noteFirestoreService.searchNote(note)

    override suspend fun getAllNotes(): List<Note>
            = noteFirestoreService.getAllNotes()

    override suspend fun insertOrUpdateNotes(notes: List<Note>) =
        noteFirestoreService.insertOrUpdateNotes(notes)


}