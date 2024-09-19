package com.example.noteapp.repository

import androidx.lifecycle.LiveData
import com.example.noteapp.database.NoteDatabase
import com.example.noteapp.model.Note

class NoteRepository(private val db: NoteDatabase) {

    suspend fun insertNote(note: Note) = db.getNoteDao().insertNote(note)
    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)
    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    suspend fun getAllNotes(): LiveData<List<Note>> = db.getNoteDao().getAllNotes()
    suspend fun searchNote(query: String?): LiveData<List<Note>> = db.getNoteDao().searchNote(query)


}