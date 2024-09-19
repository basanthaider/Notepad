package com.example.noteapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.noteapp.model.Note

@Dao
interface NoteDao {
    //if happened to be new data with the same id "ConflictStrategy.REPLACE" will replace old data with the newest
    //suspend indicates that this fun called from Corotain
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)
    @Delete
    suspend fun deleteNote(note: Note)
    @Update
    suspend fun updateNote(note: Note)
    @Query("SELECT * FROM notes ORDER BY id DESC")
     fun getAllNotes(): LiveData<List<Note>>
    @Query("SELECT * FROM notes WHERE noteTitle LIKE :query OR noteDesc LIKE :query")
     fun searchNote(query: String?): LiveData<List<Note>>

}