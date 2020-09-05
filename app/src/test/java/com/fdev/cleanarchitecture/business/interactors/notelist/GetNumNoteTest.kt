package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.domain.state.DataState
import com.fdev.cleanarchitecture.business.interactors.notelist.GetNumberOfNotes.Companion.GET_NUM_NOTES_SUCCESS
import com.fdev.cleanarchitecture.di.DependencyContainer
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetNumNoteTest {


    //System in test
    private val getNumOfNotes: GetNumberOfNotes


    //Dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteFactory = dependencyContainer.noteFactory
        getNumOfNotes = GetNumberOfNotes(
            noteCacheDataSource
        )
    }

    /*
        1.Get number of notes success and correct :
            1.1 get number of notes in cache
            1.2 listen for GET_NUM_NOTE_SUCCESS from flow emission
            1.3 compare with the number of notes in the fake data set
     */
    @InternalCoroutinesApi
    @Test
    fun getNumNotes_success_confirmCorrect() = runBlocking {
        var numNotes : Int = 0
        getNumOfNotes.getNumNotes(
            stateEvent = NoteListStateEvent.GetNumNotesInCacheEvent()
        ).collect(object : FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    GET_NUM_NOTES_SUCCESS
                )

                numNotes = value?.data?.numNotesInCache ?: 0
            }

        })

        val actualNumNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue{actualNumNotesInCache == numNotes}
    }
}