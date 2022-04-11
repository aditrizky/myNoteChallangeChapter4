package com.binar.mynote.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var title: String?=null,
    var note: String?=null
):Parcelable
