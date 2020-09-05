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

class DeleteMultipleNotes (
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    private var onDeleteError:Boolean = false

    companion object{
        val DELETE_NOTES_SUCCESS = "Successfully deleted notes."
        val DELETE_NOTES_ERRORS = "Not all the notes you selected were deleted. There was some errors."
        val DELETE_NOTES_YOU_MUST_SELECT = "You haven't selected any notes to delete."
        val DELETE_NOTES_ARE_YOU_SURE = "Are you sure you want to delete these?"
    }

    fun deleteNotes(
        notes : List<Note>,
        stateEvent : StateEvent
    ) : Flow<DataState<NoteListViewState>?> = flow{

        val successfulDeletes : ArrayList<Note> = ArrayList()
        for(note in notes){
            val cacheResult = safeCacheCall(IO){
                noteCacheDataSource.deleteNote(note.id)
            }

            val response = object : CacheResponseHandler<NoteListViewState,Int>(
                response = cacheResult,
                stateEvent = stateEvent
            ){
                override fun handleSuccess(resultObj: Int): DataState<NoteListViewState>? {
                    if(resultObj < 0){
                        onDeleteError = true
                    }else{
                        successfulDeletes.add(note)
                    }
                    return null
                }

            }.getResult()

            if(response?.stateMessage?.response?.message?.contains(stateEvent.errorInfo()) == true){
                onDeleteError = true
            }
        }

        if(onDeleteError){
            emit(
                DataState.data(
                    response = Response(
                        message = DELETE_NOTES_ERRORS,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Success()
                    ),
                    stateEvent = stateEvent
                )
            )
        }else{
            emit(
                DataState.data(
                    response = Response(
                        message = DELETE_NOTES_SUCCESS,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ),
                    stateEvent = stateEvent
                )
            )
        }

        updateNetwork(successfulDeletes)
    }

    private suspend fun updateNetwork(successfulDeletes: ArrayList<Note>) {
        for(note in successfulDeletes){
            safeApiCall(IO){
                noteNetworkDataSource.deleteNote(note.id)
            }

            safeApiCall(IO){
                noteNetworkDataSource.insertDeletedNote(note)
            }
        }
    }
}