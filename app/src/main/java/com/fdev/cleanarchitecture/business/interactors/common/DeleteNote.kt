package com.fdev.cleanarchitecture.business.interactors.common

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

class  DeleteNote<ViewState>(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    companion object{
        const val DELETE_NOTE_SUCCESS = "Succesfully deleted the note"
        const val DELETE_NOTE_FAILURE = "Failed to delete the note"
        val DELETE_NOTE_PENDING = "Delete pending..."
        val DELETE_ARE_YOU_SURE = "Are you sure you want to delete this?"
    }

    fun deleteNote(
        note : Note,
        stateEvent: StateEvent
    ) : Flow<DataState<ViewState>?> = flow{
        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.deleteNote(note.id)
        }
        val response = object : CacheResponseHandler<ViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent,
        ){
            override fun handleSuccess(resultObj: Int): DataState<ViewState> {
                return if(resultObj > 0 ){
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.None(),
                            messageType =  MessageType.Success()
                        ),
                        stateEvent = stateEvent
                    )
                }else{
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_FAILURE,
                            uiComponentType = UIComponentType.Toast(),
                            messageType =  MessageType.Error()
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()

        emit(response)

        if(response?.stateMessage?.response?.message.equals(DELETE_NOTE_SUCCESS)){
            updateNetwork(note)
        }

    }

    private suspend fun updateNetwork(note : Note){
        safeApiCall(IO){
            noteNetworkDataSource.deleteNote(note.id)
        }

        safeApiCall(IO){
            noteNetworkDataSource.insertDeletedNote(note)
        }
    }

}