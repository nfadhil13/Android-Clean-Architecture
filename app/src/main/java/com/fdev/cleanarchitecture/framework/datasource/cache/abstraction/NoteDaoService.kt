package com.fdev.cleanarchitecture.framework.datasource.cache.abstraction

import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.framework.datasource.database.NOTE_PAGINATION_PAGE_SIZE

interface NoteDaoService{
    suspend fun insertNote(note : Note) : Long

    suspend fun deleteNote(primaryKey : String) : Int

    suspend fun deleteNotes(notes : List<Note>) : Int

    suspend fun updateNote(primaryKey: String, newTitle: String, newBody: String) : Int

    suspend fun searchNote(): List<Note>

    suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNoteById(primaryKey : String) : Note?

    suspend fun getNumNotes() : Int

    suspend fun insertNotes(notes: List<Note>) : LongArray

    suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Note>
}