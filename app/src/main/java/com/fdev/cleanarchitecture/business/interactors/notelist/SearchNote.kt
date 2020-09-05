package com.fdev.cleanarchitecture.business.interactors.notelist

import com.fdev.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.util.safeCacheCall
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.state.*
import com.fdev.cleanarchitecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchNote(
    private val noteCacheDataSource: NoteCacheDataSource
) {

    companion object {
        const val SEARCH_NOTES_SUCCESS = "Successfully retrieved list of notes"
        const val SEARCH_NOTES_NO_MATCHING_RESULTS = "There are no notes that match query"
        const val SEARCH_NOTES_FAILED = "Failed to retrieve the list of notes"
    }


    fun searchNotes(
        query: String,
        filterAndOrder: String,
        page: Int,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {
        var updatedPage = page
        if (page <= 0) {
            updatedPage = 1
        }

        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.searchNotes(
                query = query,
                filterAndOrder = filterAndOrder,
                page = updatedPage
            )
        }

        val response = object : CacheResponseHandler<NoteListViewState, List<Note>>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override fun handleSuccess(resultObj: List<Note>): DataState<NoteListViewState> {
                var message : String? = SEARCH_NOTES_SUCCESS
                var uiComponentType : UIComponentType = UIComponentType.None()
                if(resultObj.size == 0){
                    message = SEARCH_NOTES_NO_MATCHING_RESULTS
                    uiComponentType = UIComponentType.Toast()
                }
                return DataState.data(
                    response = Response(
                        message = message,
                        messageType = MessageType.Success(),
                        uiComponentType = uiComponentType
                    ),
                    data = NoteListViewState(
                        noteList = ArrayList(resultObj)
                    ),
                    stateEvent = stateEvent
                )
            }

        }.getResult()

        emit(response)
    }
}