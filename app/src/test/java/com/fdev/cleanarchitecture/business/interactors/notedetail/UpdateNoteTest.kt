package com.fdev.cleanarchitecture.business.interactors.notedetail

import com.fdev.cleanarchitecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.fdev.cleanarchitecture.business.data.cache.FORCE_UPDATE_NOTE_EXCEPTION
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.domain.state.DataState
import com.fdev.cleanarchitecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_FAILURE
import com.fdev.cleanarchitecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_SUCCESS
import com.fdev.cleanarchitecture.di.DependencyContainer
import com.fdev.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailStateEvent
import com.fdev.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailViewState
import com.fdev.cleanarchitecture.util.printLogD
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*


@InternalCoroutinesApi
class UpdateNoteTest{


    //System in test
    private val updateNote : UpdateNote


    //Dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        updateNote = UpdateNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    /*
        1. Update note succes case , and cofirm the network is updated
            1.1 Select a random note from the cache
            1.2 update that note
            1.3 Confirm UPDATE_NOTE_SUCCESS  is emitid from flow
            1.4 confirm note is updated in network
            1.4 confirm note is updated in cache
     */
    @Test
    fun updateNote_success_confirmNetworkAndCacheUpdated() = runBlocking {

        val randomNote = noteCacheDataSource.searchNotes("", "", 1)
            .get(0)
        val updatedNote = Note(
            id = randomNote.id,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            updated_at = dependencyContainer.dateUtil.getCurrentTimestampString(),
            created_at = randomNote.created_at
        )
        updateNote.updateNote(
            note = updatedNote,
            stateEvent = NoteDetailStateEvent.UpdateNoteEvent()
        ).collect(object: FlowCollector<DataState<NoteDetailViewState>?>{
            override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message,
                    UPDATE_NOTE_SUCCESS
                )
            }
        })



        // confirm cache was updated
        val cacheNote = noteCacheDataSource.searchNoteById(updatedNote.id)
        assertTrue { cacheNote == updatedNote }

        // confirm that network was updated
        val networkNote = noteNetworkDataSource.searchNote(updatedNote)
        assertTrue { networkNote == updatedNote }
    }

    /*
        2. Update note fail _ confirm network and cache unchanged
            2.1 attempt to update unexist note
            2.2 check for UPDATE_NOTE_FAILURE from flow
            2.3 confirm cache not updated
            2.4 confirm network not updated
     */

    @Test
    fun updateNote_fail_confirmNetworkAndCacheUnchanged() = runBlocking{
        val unExiestID = "UNEXIST"

        val willBeUpdatedNote = noteCacheDataSource.searchNoteById(unExiestID)

        val updatedNote = noteFactory.createSingleNote(
            willBeUpdatedNote?.id,
            "New Title",
            "New Body"
        )

        val beforeUpdateCacheSize =
                noteCacheDataSource.getNumNotes()

        val beforeUpdateNetworkSize =
                noteNetworkDataSource.getAllNotes().size

        updateNote.updateNote(
            updatedNote ,
            NoteDetailStateEvent.UpdateNoteEvent()
        ).collect(object : FlowCollector<DataState<NoteDetailViewState>?>{
            override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                printLogD("DeleteNoteTest" , "After Update : ${noteCacheDataSource.searchNoteById(unExiestID)}")
                assertEquals(
                    value?.stateMessage?.response?.message,
                    UPDATE_NOTE_FAILURE
                )
            }
        })
        val afterUpdateCacheSize =
            noteCacheDataSource.getNumNotes()

        val afterUpdateNetworkSize =
            noteNetworkDataSource.getAllNotes().size

        //Confirm cache not updated
        assertEquals(beforeUpdateCacheSize , afterUpdateCacheSize)

        //confirm network not updated
        assertEquals(beforeUpdateNetworkSize, afterUpdateNetworkSize)
    }


    /*
        3. throw Exception , check generic error , and confirm network unchanged
            3.1 Attemp to force exception
            3.2 check for failure message from flow emission
            3.3 confirm cache not updated
            2.4 confirm network not updated

     */

    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged() = runBlocking{
        val updatedNote = noteFactory.createSingleNote(
            id =FORCE_UPDATE_NOTE_EXCEPTION,
            title ="New Title",
            body ="New Body"
        )

        val beforeUpdateCacheSize =
            noteCacheDataSource.getNumNotes()

        val beforeUpdateNetworkSize =
            noteNetworkDataSource.getAllNotes().size

        updateNote.updateNote(
            updatedNote ,
            NoteDetailStateEvent.UpdateNoteEvent()
        ).collect(object : FlowCollector<DataState<NoteDetailViewState>?>{
            override suspend fun emit(value: DataState<NoteDetailViewState>?) {
                printLogD("UpdateNoteTest " , "${value?.stateMessage?.response?.message}")
                assertTrue(
                    value?.stateMessage?.response?.message
                        ?.contains(CACHE_ERROR_UNKNOWN) ?: false
                )
            }
        })

        val afterUpdateCacheSize =
            noteCacheDataSource.getNumNotes()

        val afterUpdateNetworkSize =
            noteNetworkDataSource.getAllNotes().size

        //Confirm cache not updated
        assertEquals(beforeUpdateCacheSize , afterUpdateCacheSize)

        //confirm network not updated
        assertEquals(beforeUpdateNetworkSize, afterUpdateNetworkSize)
    }
}