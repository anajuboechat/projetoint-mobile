package com.projeto.medvest.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Flashcard(
    val id: String = "",
    val frente: String = "",
    val tras: String = ""
) : Parcelable
