package com.fdev.cleanarchitecture.di

import android.app.Application
import androidx.room.Room
import com.fdev.cleanarchitecture.business.domain.model.NoteFactory
import com.fdev.cleanarchitecture.framework.datasource.cache.database.NoteDatabase
import com.fdev.cleanarchitecture.framework.datasource.data.NoteDataFactory
import com.fdev.cleanarchitecture.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@Module
object TestModule {

    @FlowPreview
    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDb(app: TestBaseApplication) : NoteDatabase {
        return Room
            .inMemoryDatabaseBuilder(app, NoteDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore() : FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.useEmulator("10.0.2.2", 8080)

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }

    @FlowPreview
    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDataFactory(
        application: TestBaseApplication,
        noteFactory: NoteFactory
    ) : NoteDataFactory{
        return NoteDataFactory(
            application = application,
            noteFactory = noteFactory
        )
    }

}
