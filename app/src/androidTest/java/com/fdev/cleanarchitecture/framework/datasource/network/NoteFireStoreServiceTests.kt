package com.fdev.cleanarchitecture.framework.datasource.network


import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.fdev.cleanarchitecture.di.TestAppComponent
import com.fdev.cleanarchitecture.framework.BaseTest
import com.fdev.cleanarchitecture.framework.datasource.data.NoteDataFactory
import com.fdev.cleanarchitecture.framework.datasource.network.abstraction.NoteFirestoreService
import com.fdev.cleanarchitecture.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.fdev.cleanarchitecture.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.NOTE_COLLECTION
import com.fdev.cleanarchitecture.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.USER_ID
import com.fdev.cleanarchitecture.framework.datasource.network.mapper.NoteNetworkMapper
import com.fdev.cleanarchitecture.util.printLogD
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteFirestoreServiceTests : BaseTest(){

    // system in test
    private lateinit var noteFirestoreService: NoteFirestoreService


    override fun injectTest() {
        (application.appComponent as TestAppComponent)
            .inject(this)

    }

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var noteFactory: NoteDataFactory

    @Inject
    lateinit var networkMapper: NoteNetworkMapper



    @Before
    fun before(){
        noteFirestoreService = NoteFirestoreServiceImpl(
            firebaseAuth = FirebaseAuth.getInstance(),
            firestore = firestore,
            networkNoteMapper = networkMapper
        )
    }

    private fun signIn() = runBlocking{
        firebaseAuth.signInWithEmailAndPassword(
            EMAIL,
            PASSWORD
        ).await()
    }
    init{
        injectTest()
        signIn()
        insertTestData()
    }
    @Test
    fun queryAllNotes() = runBlocking{
        val notes = noteFirestoreService.getAllNotes()
        assertTrue{
            notes.size == 10
        }
    }

    private fun insertTestData(){
        val entityList = networkMapper.noteListToEntityList(
            noteFactory.produceListOfNotes()
        )
        for(entity in entityList){
            firestore
                .collection(NOTE_COLLECTION)
                .document(USER_ID)
                .collection(NOTE_COLLECTION)
                .document(entity.id)
                .set(entity)

        }
    }

    companion object{
        const val EMAIL = "test@gmail.com"
        const val PASSWORD = "password"
    }
}