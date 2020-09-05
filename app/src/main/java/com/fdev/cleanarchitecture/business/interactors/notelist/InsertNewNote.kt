package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.data.util.safeApiCall
import com.fdev.cleanarchitecture.business.data.util.safeCacheCall
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.domain.state.*
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertNewNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
    private val noteFactory: NoteFactory
) {

    companion object {
        const val INSERT_NOTE_SUCCESS = "Successfully inserted new note"
        const val INSERT_NOTE_FAILED = "Failed to insert new note"
    }

    fun insertNewNote(
        id: String? = null,
        title: String,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {
        val newNote = noteFactory.createSingleNote(
            id = id,
            title = title,
            body = ""
        )

        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.insertNote(newNote)
        }


        val cacheResponse = object : CacheResponseHandler<NoteListViewState,Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override fun handleSuccess(resultObj: Long): DataState<NoteListViewState>?{
                if (resultObj > 0) {
                    val viewState = NoteListViewState(
                        newNote = newNote
                    )
                    return  DataState.data(
                        response = Response(
                            message = INSERT_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }else{
                    return  DataState.data(
                        response = Response(
                            message = INSERT_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()



        emit(cacheResponse)

        updateNetwork(cacheResponse?.stateMessage?.response?.message , newNote)
    }

    private  suspend fun updateNetwork(cacheResponse: String?, newNote: Note) {
        if(cacheResponse.equals(INSERT_NOTE_SUCCESS)){
            safeApiCall(IO){
                noteNetworkDataSource.insertOrUpdateNote(newNote)
            }
        }
    }
}