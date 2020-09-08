package com.fdev.cleanarchitecture.framework.datasource.cache

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.business.domain.util.DateUtil
import com.fdev.cleanarchitecture.di.TestAppComponent
import com.fdev.cleanarchitecture.framework.BaseTest
import com.fdev.cleanarchitecture.framework.datasource.cache.abstraction.NoteDaoService
import com.fdev.cleanarchitecture.framework.datasource.cache.database.NoteDao
import com.fdev.cleanarchitecture.framework.datasource.cache.implementation.NoteDaoServiceImpl
import com.fdev.cleanarchitecture.framework.datasource.cache.util.NoteCacheMapper
import com.fdev.cleanarchitecture.framework.datasource.data.NoteDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteDaoServiceTest : BaseTest(){

    //Sytem in test
    private var noteDaoService : NoteDaoService

    @Inject
    lateinit var dao : NoteDao

    @Inject
    lateinit var noteDataFactory: NoteDataFactory

    @Inject
    lateinit var dateUtil : DateUtil

    @Inject
    lateinit var cacheMapper : NoteCacheMapper


    init{
        injectTest()
        insertTestData()
        noteDaoService = NoteDaoServiceImpl(
            noteDao = dao,
            mapper = cacheMapper,
            dateUtil = dateUtil
        )
    }

    private fun insertTestData() = runBlocking{
        val entityList = cacheMapper.noteListToEntityList(
            noteDataFactory.produceListOfNotes()
        )
        dao.insertNotes(entityList)
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

    //1 . Confirm db not zero and working
    @Test
    fun a_confirm_database_notEmpty() = runBlocking{
        val currentNoteDBSize = noteDaoService.getAllNotes().size
        assertTrue{
            currentNoteDBSize > 0
        }
    }

    //2. search a new note and searche added not
    @Test
    fun b_insert_aNewNote_and_searchAddedNote() = runBlocking{
        val newNote = noteDataFactory.createSingleNote(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )

        noteDaoService.insertNote(newNote)

        //Search the adde note in the note
        assertEquals(newNote , noteDaoService.searchNoteById(newNote.id))

    }

    /*3. insert a list of note with 1000 length ,
        inserted list length same
       and confirm filtered search query work correctly
     */

    @Test
    fun c_insert_thousandNote_test_filteredSearch() = runBlocking {
        val numberOfAddedNotes = 1000
        val expectedNumberOfNotes = noteDaoService.getAllNotes().size + numberOfAddedNotes
        val filterQuery ="a"
        val noteList = noteDataFactory.creatListOfNote(numberOfAddedNotes)
        noteDaoService.insertNotes(noteList)

        val newNumberOfNotes = noteDaoService.getAllNotes().size

        val searchAndFilteredNote = noteDaoService.searchNotesOrderByDateASC(
            filterQuery,1
        )

        //Confirm that 1000 notes have already added
        assertEquals( expectedNumberOfNotes , newNumberOfNotes)




        //confirm that note has been filtered and ordered  Asc
        val isFiltered = searchAndFilteredNote[0].title.contains(filterQuery)

        assertTrue(isFiltered)

        var isOrdered = true
        for (i in 0 until searchAndFilteredNote.size){
            for (j in 1 until searchAndFilteredNote.size){
                if(searchAndFilteredNote[i].updated_at <= searchAndFilteredNote[j].updated_at){
                    isOrdered = true
                }else{
                    isOrdered = false
                }
            }
        }

        assertTrue(isOrdered)


    }

    //4 delete new note and confirm that the note is deleted
    @Test
    fun d_DeleteNote_confrimNoteDeleted() = runBlocking{
        val preNoteList = noteDaoService.getAllNotes()


        val preNoteListSize = preNoteList.size

        val willBeDeletedNote = preNoteList[0]

        noteDaoService.deleteNote(willBeDeletedNote.id)

        val afterNoteList = noteDaoService.getAllNotes()

        val afterNoteListSize = afterNoteList.size

        //Test if the note size increased
        assertTrue{
            afterNoteListSize < preNoteListSize
        }

        val searchedDeletedNote = noteDaoService.searchNoteById(willBeDeletedNote.id)

        assertTrue{
             searchedDeletedNote == null
        }

    }

    //5 delete list of note and search if any of the list is still exist in the db
    @Test
    fun e_delete_listOfNote_and_Search_IfAnyIsExist()= runBlocking{
        val preNoteList = noteDaoService.getAllNotes()


        //Prevent out of index
        val numberOfWillBeDeletedNotes = preNoteList.size / 2

        val willBeDeletedNoteList = preNoteList.subList(0 , numberOfWillBeDeletedNotes)

        noteDaoService.deleteNotes(willBeDeletedNoteList)

        var isAllDeleted = true

        for(deleteNote in willBeDeletedNoteList){
            noteDaoService.searchNoteById(deleteNote.id)?.let{
                isAllDeleted = false
            }
        }

        assertTrue(isAllDeleted)
    }

    //6. Update db and confirm that the db is updated
    @Test
    fun f_update_db_andConfirm_db_updated() = runBlocking {

        val existingNoteID = "2474abea-7584-486b-9f88-87a21870b0ec"

        val newTitle = "New title"

        val newBody = "New Body"

        val newNote = noteDaoService.searchNoteById(existingNoteID)!!


        noteDaoService.updateNote(
            newNote.id,
            newTitle,
            newBody
        )

        val updatedNote = noteDaoService.searchNoteById(newNote.id)!!

        assertTrue{
            updatedNote.title.equals(newTitle) && updatedNote.body.equals(newBody)
        }

    }

    //7. search notes, ordered by date ASC , confirm order
    @Test
    fun searchNotes_orderedDate_ASC_confirmOrder() = runBlocking {
        val filterQuery ="a"
        val searchAndFilteredNote = noteDaoService.searchNotesOrderByDateASC(
            filterQuery,1
        )

        //confirm that note has been filtered and ordered  Asc
        val isFiltered = searchAndFilteredNote[0].title.contains(filterQuery)

        assertTrue(isFiltered)

        var isOrdered = true
        for (i in 0 until searchAndFilteredNote.size){
            for (j in 1 until searchAndFilteredNote.size){
                if(searchAndFilteredNote[i].updated_at <= searchAndFilteredNote[j].updated_at){
                    isOrdered = true
                }else{
                    isOrdered = false
                }
            }
        }

        assertTrue(isOrdered)
    }

    //8 search notes, ordered by date DESC , confirm order
    @Test
    fun searchNotes_orderedDate_DESC_confirmOrder() = runBlocking {
        val filterQuery ="a"
        val searchAndFilteredNote = noteDaoService.searchNotesOrderByDateDESC(
            filterQuery,1
        )

        //confirm that note has been filtered and ordered  Asc
        val isFiltered = searchAndFilteredNote[0].title.contains(filterQuery)

        assertTrue(isFiltered)

        var isOrdered = true
        for (i in 0 until searchAndFilteredNote.size){
            for (j in 1 until searchAndFilteredNote.size){
                if(searchAndFilteredNote[i].updated_at >= searchAndFilteredNote[j].updated_at){
                    isOrdered = true
                }else{
                    isOrdered = false
                }
            }
        }

        assertTrue(isOrdered)
    }

    //9. search notes, ordered by title ASC , confirm order
    @Test
    fun searchNotes_orderedTitle_ASC_confirmOrder() = runBlocking {
        val filterQuery ="a"
        val searchAndFilteredNote = noteDaoService.searchNotesOrderByTitleASC(
            filterQuery,1
        )

        //confirm that note has been filtered and ordered  Asc
        val isFiltered = searchAndFilteredNote[0].title.contains(filterQuery)

        assertTrue(isFiltered)

        var isOrdered = true
        for (i in 0 until searchAndFilteredNote.size){
            for (j in 1 until searchAndFilteredNote.size){
                if(searchAndFilteredNote[i].title <= searchAndFilteredNote[j].title){
                    isOrdered = true
                }else{
                    isOrdered = false
                }
            }
        }

        assertTrue(isOrdered)
    }

    //10 search notes, ordered by title DESC , confirm order
    @Test
    fun searchNotes_orderedTitle_DESC_confirmOrder() = runBlocking {
        val filterQuery ="a"
        val searchAndFilteredNote = noteDaoService.searchNotesOrderByTitleDESC(
            filterQuery,1
        )

        //confirm that note has been filtered and ordered  Asc
        val isFiltered = searchAndFilteredNote[0].title.contains(filterQuery)

        assertTrue(isFiltered)

        var isOrdered = true
        for (i in 0 until searchAndFilteredNote.size){
            for (j in 1 until searchAndFilteredNote.size){
                if(searchAndFilteredNote[i].title >= searchAndFilteredNote[j].title){
                    isOrdered = true
                }else{
                    isOrdered = false
                }
            }
        }

        assertTrue(isOrdered)
    }



}