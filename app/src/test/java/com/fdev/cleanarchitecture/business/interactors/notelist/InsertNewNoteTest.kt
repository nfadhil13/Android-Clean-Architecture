package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.fdev.cleanarchitecture.business.data.cache.FORCE_GENERAL_FAILURE
import com.fdev.cleanarchitecture.business.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.domain.state.DataState
import com.fdev.cleanarchitecture.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_FAILED
import com.fdev.cleanarchitecture.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_SUCCESS
import com.fdev.cleanarchitecture.di.DependencyContainer
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

/*
Test cases :
1. insertNote_success_confirmNetworkAndCacheUpdated()
    1.1 Insert new note
    1.2 Listen for INSERT_NOTE_SUCCESS emission from flow
    1.3 Confirm cache was updated with new note
    1.4 confirm network was updated with new note

2. intertNote_fail_confirmNetworkAndCacheUnchanged()
    2.1 Insert a new note
    2.2 force a failuer ( return -1 from db operation)
    2.3 listen for INSERT_NOTE_FAILED emission from flow
    2.4 confirm cache was not updated
    2.5 confirm networ was not updated

3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    3.1 insert a new note
    3.2 force an exception
    3.3 listen for CACHE_ERROR_UNKNOWN emission from flow
    3.4 confirm cache was not updated
    3.5 confirm networ was not updated
 */
@InternalCoroutinesApi
class InsertNewNoteTest {

    //System in test
    private val insertNewNote : InsertNewNote


    //Dependencies
    private val dependencyContainer : DependencyContainer
    private val noteCacheDataSource : NoteCacheDataSource
    private val noteNetworkDataSource : NoteNetworkDataSource
    private val noteFactory : NoteFactory

    init{
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        insertNewNote = InsertNewNote(
            noteCacheDataSource,
            noteNetworkDataSource,
            noteFactory
        )
    }


    @Test
    fun insertNote_success_confirmNetworkAndCacheUpdated(): Unit = runBlocking{
        val newNote = noteFactory.createSingleNote(
            id = null,
            title = UUID.randomUUID().toString()
        )

        insertNewNote.insertNewNote(
            id = newNote.id,
            title = newNote.title,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(title = newNote.title)
        ).collect(object: FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_SUCCESS
                )
            }

        })

        //confirm cache was updated
        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue{cacheNoteThatWasInserted == newNote}

        //confirm network was updated
        val networkThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue{networkThatWasInserted == newNote}

    }

    @Test
    fun intertNote_fail_confirmNetworkAndCacheUnchanged() = runBlocking{
        val newNote = noteFactory.createSingleNote(
            id = null,
            title = UUID.randomUUID().toString()
        )

        insertNewNote.insertNewNote(
            id = FORCE_GENERAL_FAILURE,
            title = newNote.title,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(title = newNote.title)
        ).collect(object: FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    INSERT_NOTE_FAILED
                )
            }

        })

        //confirm cache was not updated
        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue{cacheNoteThatWasInserted == null}

        //confirm network was not updated
        val networkThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue{networkThatWasInserted == null}
    }


    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged() = runBlocking{
        val newNote = noteFactory.createSingleNote(
            id = null,
            title = UUID.randomUUID().toString()
        )

        insertNewNote.insertNewNote(
            id = FORCE_NEW_NOTE_EXCEPTION,
            title = newNote.title,
            stateEvent = NoteListStateEvent.InsertNewNoteEvent(title = newNote.title)
        ).collect(object: FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message?.contains(
                        CACHE_ERROR_UNKNOWN
                    )?: false
                )
            }

        })

        //confirm cache was not updated
        val cacheNoteThatWasInserted = noteCacheDataSource.searchNoteById(newNote.id)
        assertTrue{cacheNoteThatWasInserted == null}

        //confirm network was not updated
        val networkThatWasInserted = noteNetworkDataSource.searchNote(newNote)
        assertTrue{networkThatWasInserted == null}
    }
}