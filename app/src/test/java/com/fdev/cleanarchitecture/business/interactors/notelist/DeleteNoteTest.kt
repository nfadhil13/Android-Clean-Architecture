package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.fdev.cleanarchitecture.business.data.cache.FORCE_DELETES_NOTE_EXCEPTION
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.domain.state.DataState
import com.fdev.cleanarchitecture.business.interactors.common.DeleteNote
import com.fdev.cleanarchitecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_FAILURE
import com.fdev.cleanarchitecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_SUCCESS
import com.fdev.cleanarchitecture.di.DependencyContainer
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListStateEvent
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import com.fdev.cleanarchitecture.util.printLogD
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
class DeleteNoteTest {

    //System in test
    private val deleteNote: DeleteNote<NoteListViewState>


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
        deleteNote = DeleteNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    /*
        1.Delete note cache success and confirm network also updated
            1.1 delete a note
            1.2 check for success message from flow from cache emission
            1.3 confirm note was deleted in cache
            1.4 confirm note was deleted in Network
            1.5 confirm note was added to "deletedNote" in network
     */

    @Test
    fun deleteNote_success_confirmNetworkUpdated() = runBlocking {

        val existId = "2474abaa-788a-4a6b-948z-87a2167hb0ec"

        val willBeDeletedNote = noteCacheDataSource.searchNoteById(existId)

        willBeDeletedNote?.let { note ->
            deleteNote
                .deleteNote(
                    note,
                    NoteListStateEvent.DeleteNoteEvent(note)
                )
                .collect(object : FlowCollector<DataState<NoteListViewState>?> {
                    override suspend fun emit(value: DataState<NoteListViewState>?) {
                        assertEquals(
                            value?.stateMessage?.response?.message,
                            DELETE_NOTE_SUCCESS
                        )
                    }

                })
        }

        //Is note deleted from cache
        val isNoteDeletedAtCache =
            noteCacheDataSource.searchNoteById(existId) == null


        assertTrue { isNoteDeletedAtCache }

        //Is not deleted from network
        val isNoteDeletedAtNetwork =
            noteNetworkDataSource.searchNote(willBeDeletedNote!!) == null

        assertTrue(isNoteDeletedAtNetwork)

        //Is note added to deleted note in network
        val isNoteAddedToDeletedNoteInNetwork =
            noteNetworkDataSource.getDeletedNotes().contains(willBeDeletedNote)

        assertTrue(isNoteAddedToDeletedNoteInNetwork)

    }


    /*
        2.deleteNote fail , confirm network unchanged
            2.1 Attempt to delete a note , and fail because not isnt exist
            2.2 Check for failuter message from flow emission
            2.3 confirm network was not changed
            
     */

    @Test
    fun deleteNote_fail_confirmNetworkUnchanged() = runBlocking {
        val unexiestID = "UNEXIST ID"
        val willBeDeletedNote = noteCacheDataSource.searchNoteById(unexiestID)

        //Network status before any delete attempt
        val preNetworkNotes = noteNetworkDataSource.getAllNotes()
        val preNetworkDeletedNotes = noteNetworkDataSource.getDeletedNotes()

        willBeDeletedNote?.let { note ->
            deleteNote
                .deleteNote(
                    note,
                    NoteListStateEvent.DeleteNoteEvent(note)
                )
                .collect(object : FlowCollector<DataState<NoteListViewState>?> {
                    override suspend fun emit(value: DataState<NoteListViewState>?) {
                        assertEquals(
                            value?.stateMessage?.response?.message,
                            DELETE_NOTE_FAILURE
                        )
                    }

                })
        }


        //Confirm note isnt changed
        val isNetworkChange =
            preNetworkNotes == noteNetworkDataSource.getAllNotes() &&
                    preNetworkDeletedNotes == noteNetworkDataSource.getDeletedNotes() &&
                    noteCacheDataSource.getNumNotes() == noteNetworkDataSource.getAllNotes().size

        assertTrue(isNetworkChange)


    }

    /*
    2.throwException_checkGenericError_confirmNetworkUnChanged()
        2.1 Attempt to delete a note , forche an exception to throw
        2.2 Check for failure message from flow emission
        2.3 confirm network was not changed

 */
    @Test
    fun throwException_checkGenericError_confirmNetworkUnChanged() = runBlocking {

        val forceDeleteNote = Note(
            id = FORCE_DELETES_NOTE_EXCEPTION,
            title = "TEST TITLE",
            body = "TEST BODY",
            created_at = "TEST TIME",
            updated_at = "TEST UPDATE TIME"
        )


        //Network status before any delete attempt
        val prevNetworkNotes = noteNetworkDataSource.getAllNotes()
        val prevNetworkDeletedNotes = noteNetworkDataSource.getDeletedNotes()


        deleteNote
            .deleteNote(forceDeleteNote, NoteListStateEvent.DeleteNoteEvent(forceDeleteNote))
            .collect(object : FlowCollector<DataState<NoteListViewState>?> {
                override suspend fun emit(value: DataState<NoteListViewState>?) {
                    assertTrue(
                        value?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN)
                            ?: false
                    )
                }

            })


        //Confirm note isnt changed
        val isNetworkChange =
            prevNetworkNotes == noteNetworkDataSource.getAllNotes() &&
                    prevNetworkDeletedNotes == noteNetworkDataSource.getDeletedNotes() &&
                    noteCacheDataSource.getNumNotes() == noteNetworkDataSource.getAllNotes().size



        assertTrue(isNetworkChange)


    }

}