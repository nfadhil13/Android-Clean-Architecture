package com.fdev.cleanarchitecture.framework.datasource.cache.implementation

import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.util.DateUtil
import com.fdev.cleanarchitecture.framework.datasource.cache.abstraction.NoteDaoService
import com.fdev.cleanarchitecture.framework.datasource.cache.database.NoteDao
import com.fdev.cleanarchitecture.framework.datasource.cache.database.returnOrderedQuery
import com.fdev.cleanarchitecture.framework.datasource.cache.util.NoteCacheMapper
import javax.inject.Inject

class NoteDaoServiceImpl
@Inject
constructor(
    private val noteDao : NoteDao ,
    private val mapper : NoteCacheMapper,
    private val dateUtil : DateUtil
):NoteDaoService{
    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(mapper.mapToEntity(note))
    }

    override suspend fun deleteNote(primaryKey: String): Int {
        return noteDao.deleteNote(primaryKey)
    }

    override suspend fun deleteNotes(notes: List<Note>): Int {
        val ids = notes.mapIndexed { index, note -> note.id }
        return noteDao.deleteNotes(ids)
    }

    override suspend fun updateNote(primaryKey: String, newTitle: String, newBody: String?
        , updated_at : String?): Int {
       return noteDao.updateNote(
           primaryKey= primaryKey,
           title = newTitle,
           body = newBody,
           updated_at = updated_at ?: dateUtil.getCurrentTimestampString()
       )
    }

    override suspend fun searchNote(): List<Note> {
        return mapper.entityListToNoteList(
            noteDao.searchNotes()
        )
    }

    override suspend fun getAllNotes(): List<Note> {
        return mapper.entityListToNoteList(
            noteDao.searchNotes()
        )
    }

    override suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return mapper.entityListToNoteList(
            noteDao.searchNotesOrderByDateDESC(
                query = query,
                page = page
            )
        )
    }

    override suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return mapper.entityListToNoteList(
            noteDao.searchNotesOrderByDateASC(
                query = query,
                page = page
            )
        )
    }

    override suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return mapper.entityListToNoteList(
            noteDao.searchNotesOrderByTitleDESC(
                query = query,
                page = page
            )
        )
    }

    override suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return mapper.entityListToNoteList(
            noteDao.searchNotesOrderByTitleASC(
                query = query,
                page = page
            )
        )
    }

    override suspend fun searchNoteById(primaryKey: String): Note? {
        return noteDao.searchNoteById(primaryKey)?.let{
            mapper.mapFromEntity(it)
        }
    }

    override suspend fun getNumNotes(): Int {
        return noteDao.getNumNotes()
    }

    override suspend fun insertNotes(notes: List<Note>): LongArray {
        return noteDao.insertNotes(mapper.noteListToEntityList(notes))
    }

    override suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Note> {
        return  mapper.entityListToNoteList(
            noteDao.returnOrderedQuery(
                query = query,
                page = page,
                filterAndOrder = filterAndOrder
            )
        )
    }

}