package com.fdev.cleanarchitecture.framework.datasource.network.implementation

import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.framework.datasource.network.abstraction.NoteFirestoreService
import com.fdev.cleanarchitecture.framework.datasource.network.mapper.NoteNetworkMapper
import com.fdev.cleanarchitecture.framework.datasource.network.model.NoteNetworkEntity
import com.fdev.cleanarchitecture.util.cLog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteFirestoreServiceImpl
@Inject
constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val networkNoteMapper: NoteNetworkMapper
) : NoteFirestoreService {

    companion object {
        const val NOTE_COLLECTION = "notes"
        const val USERS_COLLECTION = "users"
        const val DELETES_COLLECTION = "deletes"
        const val USER_ID = "cPz3WDvqAVZEEd6K7M9J1XveWef2"
        const val EMAIL = "test@gmail.com"
    }

    override suspend fun insertOrUpdateNote(note: Note) {
        val entityNote = networkNoteMapper.mapToEntity(note)
        firestore
            .collection(NOTE_COLLECTION)
            .document(USER_ID)
            .collection(NOTE_COLLECTION)
            .document(entityNote.id)
            .set(entityNote)
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()

    }

    override suspend fun deleteNote(primaryKey: String) {
        firestore
            .collection(NOTE_COLLECTION)
            .document(USER_ID)
            .collection(NOTE_COLLECTION)
            .document(primaryKey)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun insertDeletedNote(note: Note) {
        val entity = networkNoteMapper.mapToEntity(note)
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTE_COLLECTION)
            .document(note.id)
            .set(entity)
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun insertDeletedNotes(notes: List<Note>) {
        if (notes.size > 500) {
            throw Exception("Cannot insert more than 500 notes at a time into firestore")
        }

        val collectionRef = firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTE_COLLECTION)

        firestore.runBatch { batch ->
            for (note in notes) {
                val entity = networkNoteMapper.mapToEntity(note)
                val documentRef = collectionRef.document(note.id)
                batch.set(documentRef, entity)
            }
        }
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun deleteDeletedNote(note: Note) {
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTE_COLLECTION)
            .document(note.id)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun getDeletedNotes(): List<Note> {
        return networkNoteMapper.entityListToNoteList(
            firestore
                .collection(DELETES_COLLECTION)
                .document(USER_ID)
                .collection(NOTE_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await().toObjects(NoteNetworkEntity::class.java)
        )
    }

    override suspend fun deleteAllNotes() {
        firestore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()


        firestore
            .collection(NOTE_COLLECTION)
            .document(USER_ID)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun searchNote(note: Note): Note? {
        return firestore
            .collection(NOTE_COLLECTION)
            .document(USER_ID)
            .collection(NOTE_COLLECTION)
            .document(note.id)
            .get()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
            .toObject(NoteNetworkEntity::class.java)?.let {
                networkNoteMapper.mapFromEntity(it)
            }
    }

    override suspend fun getAllNotes(): List<Note> {
        return networkNoteMapper.entityListToNoteList(
            firestore
                .collection(NOTE_COLLECTION)
                .document(USER_ID)
                .collection(NOTE_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await().toObjects(NoteNetworkEntity::class.java)
        )
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {
        if (notes.size > 500) {
            throw Exception("Cannot insert more than 500 notes at a time into firestore")
        }

        val collectionRef = firestore
            .collection(NOTE_COLLECTION)
            .document(USER_ID)
            .collection(NOTE_COLLECTION)

        firestore.runBatch { batch ->
            for (note in notes) {
                val entity = networkNoteMapper.mapToEntity(note)
                entity.updated_at = Timestamp.now()
                val documentRef = collectionRef.document(note.id)
                batch.set(documentRef, entity)
            }
        }.addOnFailureListener {
            cLog(it.message)
        }.await()
    }

}