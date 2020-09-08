package com.fdev.cleanarchitecture.framework.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fdev.cleanarchitecture.R
import com.fdev.cleanarchitecture.util.printLogD
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity()
{

    private val TAG: String = "AppDebug"

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent
            .inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        printLogD("MainActivity" , "${firebaseAuth==null}")
    }



}