package com.fdev.cleanarchitecture.business.interactors.notedetail

import com.fdev.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.data.util.safeApiCall
import com.fdev.cleanarchitecture.business.data.util.safeCacheCall
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.state.*
import com.fdev.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailStateEvent
import com.fdev.cleanarchitecture.framework.presentation.notedetail.state.NoteDetailViewState
import com.fdev.cleanarchitecture.util.printLogD
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {
    companion object{
        const val UPDATE_NOTE_SUCCESS = "Successfully updated note."
        const val UPDATE_NOTE_FAILURE = "Failed to update note."
        const val UPDATE_NOTE_FAILED_PK = "Update failed. Note is missing primary key"
    }

    fun updateNote(
        note: Note,
        stateEvent: StateEvent
    ) : Flow<DataState<NoteDetailViewState>?> = flow{

        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.updateNote(note.id
                , note.title , note.body)
        }

        val response = object : CacheResponseHandler<NoteDetailViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override fun handleSuccess(resultObj: Int): DataState<NoteDetailViewState>? {
                return if(resultObj > 0){
                    DataState.data(
                        response = Response(
                            message = UPDATE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType =  MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }else{
                    DataState.data(
                        response = Response(
                            message = UPDATE_NOTE_FAILURE,
                            uiComponentType = UIComponentType.Toast(),
                            messageType =  MessageType.Error()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()
        emit(response)

        if(response?.stateMessage?.response?.message.equals(UPDATE_NOTE_SUCCESS)){
            updateNetwork(note)
        }

    }

    private suspend fun updateNetwork(note: Note) {
        safeApiCall(IO){
            noteNetworkDataSource.insertOrUpdateNote(note)
        }
    }

}