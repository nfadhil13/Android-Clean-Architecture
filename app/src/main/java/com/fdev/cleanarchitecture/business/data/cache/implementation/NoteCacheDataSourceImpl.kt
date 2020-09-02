package com.fdev.cleanarchitecture.business.data.cache.implementation

import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.framework.datasource.cache.abstraction.NoteDaoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteCacheDataSourceImpl
@Inject
constructor(
    private val noteDaoService: NoteDaoService
):NoteCacheDataSource {
    override suspend fun insertNote(note: Note): Long
            = noteDaoService.insertNote(note)

    override suspend fun deleteNote(primaryKey : String): Int
            = noteDaoService.deleteNote(primaryKey)


    override suspend fun deleteNotes(notes: List<Note>): Int
            = noteDaoService.deleteNotes(notes)

    override suspend fun updateNote(primaryKey: String, newTitle: String, newBody: String): Int
            = noteDaoService.updateNote(primaryKey , newTitle, newBody)

    override suspend fun searchNote(query: String, filterAndOrder: String, page: Int): List<Note>
            = noteDaoService.searchNote(query,filterAndOrder,page)


    override suspend fun searchNoteById(primaryKey: String): Note?
            = noteDaoService.searchNoteById(primaryKey)

    override suspend fun getNumNotes(): Int
            = noteDaoService.getNumNotes()

    override suspend fun insertNotes(notes: List<Note>): LongArray
            = noteDaoService.insertNotes(notes)

}