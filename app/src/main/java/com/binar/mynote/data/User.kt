package com.binar.mynote.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var name : String? =null,
    var username : String? = null,
    var password : String? = null
): Parcelable

