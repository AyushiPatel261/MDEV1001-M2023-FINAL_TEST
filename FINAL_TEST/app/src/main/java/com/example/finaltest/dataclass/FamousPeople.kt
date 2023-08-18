package com.example.finaltest.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FamousPeople(
    var documentID : String? = null,
    val peopleID: Long,
    val name : String,
    val occupation : String,
    val nationality: String,
    val birthDate: String,
    val birthPlace: String,
    val bio:String,
    val achievements: List<String>,
    val imageURL: String,

): Parcelable
{
    constructor() : this(
        "",
        0L,
        "",
        "",
        "",
        "",
       "",
        "",
        mutableListOf(),
        "",
    )
}