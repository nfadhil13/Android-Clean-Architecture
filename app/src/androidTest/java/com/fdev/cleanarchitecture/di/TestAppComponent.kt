package com.fdev.cleanarchitecture.di

import com.fdev.cleanarchitecture.business.TempTest
import com.fdev.cleanarchitecture.framework.datasource.cache.NoteDaoServiceTest
import com.fdev.cleanarchitecture.framework.datasource.network.NoteFirestoreServiceTests
import com.fdev.cleanarchitecture.framework.presentation.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        AppModule::class,
        TestModule::class
    ]
)interface TestAppComponent : AppComponent{

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance app : TestBaseApplication) : TestAppComponent
    }

    fun inject(temptTest : TempTest)

    fun inject(noteFirestoreServiceTests: NoteFirestoreServiceTests)

    fun inject(noteDaoServiceTest : NoteDaoServiceTest)
}