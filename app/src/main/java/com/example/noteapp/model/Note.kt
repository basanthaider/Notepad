package com.example.noteapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteDesc: String
) : Parcelable

/* Parceliz is a way to convert complex objects into a simple format
that can be easily transformed between different components of an application(activity or fragments)*/
