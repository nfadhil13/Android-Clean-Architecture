package com.fdev.cleanarchitecture.di

import com.fdev.cleanarchitecture.framework.presentation.BaseApplication
import com.fdev.cleanarchitecture.framework.presentation.MainActivity
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
        ProductionModule::class,
        NoteFragmentFactoryModule::class,
        NoteViewModelModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance app : BaseApplication)  : AppComponent
    }

    fun inject(mainActivity: MainActivity)
}