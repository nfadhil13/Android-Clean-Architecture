package com.fdev.cleanarchitecture.business.interactors.splash

import com.fdev.cleanarchitecture.business.data.cache.CacheResponseHandler
import com.fdev.cleanarchitecture.business.data.cache.abstraction.NoteCacheDataSource
import com.fdev.cleanarchitecture.business.data.network.ApiResponseHandler
import com.fdev.cleanarchitecture.business.data.network.abstraction.NoteNetworkDataSource
import com.fdev.cleanarchitecture.business.data.util.safeApiCall
import com.fdev.cleanarchitecture.business.data.util.safeCacheCall
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.state.DataState
import com.fdev.cleanarchitecture.business.domain.util.DateUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncNotes(
    private val noteCacheDataSource : NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
){

    suspend fun syncNotes(){

        val cachedNoteList = getCachednotes()

        val networkNoteList = getNetworkNotes()

        sycnNetworkNoteWithCachedNoted(cachedNotes =  ArrayList(cachedNoteList) ,networkNotes = ArrayList(networkNoteList))
    }

    private suspend fun getCachednotes() : List<Note>{
        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.getAllNotes()
        }

        val response = object : CacheResponseHandler<List<Note>, List<Note>>(
            response = cacheResult,
            stateEvent = null
        ){
            override fun handleSuccess(resultObj: List<Note>): DataState<List<Note>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }
        }.getResult()

        return response?.data ?: ArrayList()
    }

    private suspend fun getNetworkNotes() : List<Note>{

        val networkResult = safeApiCall(IO){
            noteNetworkDataSource.getAllNotes()
        }

        val response = object :  ApiResponseHandler<List<Note> , List<Note>>(
            response = networkResult,
            stateEvent = null
        ){
            override suspend fun handleSuccess(resultObj: List<Note>): DataState<List<Note>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }

        }.getResult()

        return response?.data ?: ArrayList()
    }

    private suspend fun sycnNetworkNoteWithCachedNoted(
        cachedNotes: ArrayList<Note>,
        networkNotes : List<Note>
    ) = withContext(IO){

        for(networkNote in networkNotes){
            noteCacheDataSource.searchNoteById(networkNote.id)?.let{cachedNote ->
                cachedNotes.remove(cachedNote)
                checkIfCachedNoteRequiresUpdate(cachedNote , networkNote)
            }?: noteCacheDataSource.insertNote(networkNote)
        }

        for(cachedNote in cachedNotes){
            safeApiCall(IO){
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }

    }

    private suspend fun checkIfCachedNoteRequiresUpdate(cachedNote: Note, networkNote: Note) {
        val cacheLastUpdate = cachedNote.updated_at
        val networkLastUpdate = networkNote.updated_at


        if(networkLastUpdate > cacheLastUpdate){
            safeCacheCall(IO){
                noteCacheDataSource.updateNote(
                    primaryKey = networkNote.id,
                    newBody = networkNote.body,
                    newTitle = networkNote.title,
                    updated_at  = networkNote.updated_at
                )
            }
        }else if(networkLastUpdate < cacheLastUpdate){
            safeApiCall(IO){
                noteNetworkDataSource.insertOrUpdateNote(
                    cachedNote
                )
            }
        }
    }

}