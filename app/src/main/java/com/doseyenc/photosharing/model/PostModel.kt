package com.doseyenc.photosharing.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostModel(
    val date: String,
    val imageUrl: String,
    val userComment: String,
    val userEmail: String
):Parcelable