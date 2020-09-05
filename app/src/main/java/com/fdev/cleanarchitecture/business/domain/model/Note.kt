package com.fdev.cleanarchitecture.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: String,
    val title: String,
    var body: String,
    val updated_at: String,
    val created_at: String
) : Parcelable