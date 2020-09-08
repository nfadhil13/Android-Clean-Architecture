package com.fdev.cleanarchitecture.framework.datasource.data

import android.app.Application
import android.content.res.AssetManager
import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteDataFactory
@Inject
constructor(
    private val application : Application,
    private val noteFactory : NoteFactory
) {

    fun produceListOfNotes(): List<Note> {
        val notes: List<Note> = Gson()
            .fromJson(
                readJSONFromAsset("note_list.json"),
                object : TypeToken<List<Note>>() {}.type
            )
        return notes
    }

    private fun readJSONFromAsset(filename: String): String? {
        return try {
            val inputStream: InputStream = (application.assets as AssetManager)
                .open(filename)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun createSingleNote(
        id: String? = null,
        title: String,
        body: String? = null
    ) = noteFactory.createSingleNote(id, title, body)

    fun creatListOfNote(
        numNotes: Int
    ) = noteFactory.createNoteList(numNotes)
}