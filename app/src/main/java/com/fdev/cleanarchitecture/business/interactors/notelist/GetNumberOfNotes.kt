package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.util.safeCacheCall
import com.fdev.cleanarchitecture.business.domain.state.*
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNumberOfNotes(
    private val noteCacheDataSource: NoteCacheDataSource
) {

    companion object{
        const val GET_NUM_NOTES_SUCCESS = "Succesfully retrieved the number of Notes"
    }

    fun getNumNotes(
        stateEvent: StateEvent
    ) : Flow<DataState<NoteListViewState>?> = flow{
        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.getNumNotes()
        }

        val response = object : CacheResponseHandler<NoteListViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override fun handleSuccess(resultObj: Int): DataState<NoteListViewState> {
                val viewState = NoteListViewState(
                    numNotesInCache = resultObj
                )
                return DataState.data(
                    response = Response(
                        message = GET_NUM_NOTES_SUCCESS,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Success()
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(response)
    }

}