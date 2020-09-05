package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.data.util.safeApiCall
import com.fdev.cleanarchitecture.business.data.util.safeCacheCall
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.state.*
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RestoreDeletedNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    companion object{

        const val  RESTORE_NOTE_SUCCESS = "Succes to restore deleted note"
        const val  RESTORE_NOTE_FAILED = "Failed to restore deleted note"

    }

    fun restoreDeletedNote(
        note : Note,
        stateEvent: StateEvent
    ) : Flow<DataState<NoteListViewState>?> = flow {
        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.insertNote(note)
        }

        val response = object : CacheResponseHandler<NoteListViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override fun handleSuccess(resultObj: Long): DataState<NoteListViewState>? {
                return if (resultObj > 0) {
                    val viewState = NoteListViewState(
                        notePendingDelete = NoteListViewState.NotePendingDelete(
                            note = note
                        )
                    )

                    DataState.data(
                        response = Response(
                            message = RESTORE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.data(
                        response = Response(
                            message = RESTORE_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()

        emit(response)

        if(response?.stateMessage?.response?.message.equals(RESTORE_NOTE_SUCCESS)){
            updateNetwork(note)
        }

    }

    private suspend fun updateNetwork(note: Note) {
        safeApiCall(IO){
            noteNetworkDataSource.insertOrUpdateNote(note)
        }

        safeApiCall(IO){
            noteNetworkDataSource.deleteDeletedNote(note)
        }
    }

}