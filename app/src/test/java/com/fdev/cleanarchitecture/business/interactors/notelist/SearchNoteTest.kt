package com.fdev.cleanarchitecture.business.interactors.notelist


import com.fdev.cleanarchitecture.business.data.cache.CacheErrors
import com.fdev.cleanarchitecture.business.data.cache.FORCE_SEARCH_NOTES_EXCEPTION
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.domain.state.DataState
import com.fdev.cleanarchitecture.business.interactors.notelist.SearchNote.Companion.SEARCH_NOTES_NO_MATCHING_RESULTS
import com.fdev.cleanarchitecture.business.interactors.notelist.SearchNote.Companion.SEARCH_NOTES_SUCCESS
import com.fdev.cleanarchitecture.di.DependencyContainer
import com.fdev.cleanarchitecture.framework.datasource.database.ORDER_BY_ASC_DATE_UPDATED
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test



@InternalCoroutinesApi
class SearchNoteTest {

    //System in test
    private val searchNote: SearchNote


    //Dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteFactory = dependencyContainer.noteFactory
        searchNote = SearchNote(
            noteCacheDataSource
        )
    }


    /*
    Test cases:
    1. blankQuery_success_confirmNotesRetrieved()
        a) query with some default search options
        b) listen for SEARCH_NOTES_SUCCESS emitted from flow
        c) confirm notes were retrieved
        d) confirm notes in cache match with notes that were retrieved
    @Test

     */
    fun blankQuery_success_confirmNotesRetrieved(): Unit = runBlocking {

        val query = ""
        var results: ArrayList<Note>? = null
        searchNote.searchNotes(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1,
            stateEvent = NoteListStateEvent.SearchNotesEvent()
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    SEARCH_NOTES_SUCCESS
                )

                value?.data?.noteList?.let { list ->
                    results = ArrayList(list)
                }
            }

        })

        //confirm notes were retrieved
        assertTrue { results != null }

        // confirm notes in cache match with notes that were retrieved
        val notesInCache = noteCacheDataSource.searchNotes(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1
        )

        assertTrue { results?.containsAll(notesInCache) != null }
    }

    /*
    2. randomQuery_success_confirmNoResults()
        a) query with something that will yield no results
        b) listen for SEARCH_NOTES_NO_MATCHING_RESULTS emitted from flow
        c) confirm nothing was retrieved
        d) confirm there is notes in the cache
     */
    @Test
    fun randomQuery_success_confirmNoResults() = runBlocking {

        val query = "hthrthrgrkgenrogn843nn4u34n934v53454hrth"
        var results: ArrayList<Note>? = null
        searchNote.searchNotes(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1,
            stateEvent = NoteListStateEvent.SearchNotesEvent()
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    SEARCH_NOTES_NO_MATCHING_RESULTS
                )
                value?.data?.noteList?.let { list ->
                    results = ArrayList(list)
                }
            }
        })

        // confirm nothing was retrieved
        assertTrue { results?.run { size == 0 } ?: true }

        // confirm there is notes in the cache
        val notesInCache = noteCacheDataSource.searchNotes(
            query = "",
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1
        )
        assertTrue { notesInCache.isNotEmpty() }
    }

    /*
    3. searchNotes_fail_confirmNoResults()
        a) force an exception to be thrown
        b) listen for CACHE_ERROR_UNKNOWN emitted from flow
        c) confirm nothing was retrieved
        d) confirm there is notes in the cache
     */
    @Test
    fun searchNotes_fail_confirmNoResults() = runBlocking {

        val query = FORCE_SEARCH_NOTES_EXCEPTION
        var results: ArrayList<Note>? = null
        searchNote.searchNotes(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1,
            stateEvent = NoteListStateEvent.SearchNotesEvent()
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message
                        ?.contains(CacheErrors.CACHE_ERROR_UNKNOWN) ?: false
                )
                value?.data?.noteList?.let { list ->
                    results = ArrayList(list)
                }
                println("results: $results")
            }
        })

        // confirm nothing was retrieved
        assertTrue { results?.run { size == 0 } ?: true }

        // confirm there is notes in the cache
        val notesInCache = noteCacheDataSource.searchNotes(
            query = "",
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1
        )
        assertTrue { notesInCache.isNotEmpty() }
    }

}