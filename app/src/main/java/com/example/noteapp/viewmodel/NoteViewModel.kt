package com.example.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.noteapp.model.Note
import com.example.noteapp.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(app: Application, private val noteRepository: NoteRepository) :
    AndroidViewModel(app) {
    fun addNote(note: Note) = viewModelScope.launch { noteRepository.insertNote(note) }
    fun deleteNote(note: Note) = viewModelScope.launch { noteRepository.deleteNote(note) }
    fun updateNOte(note: Note) = viewModelScope.launch { noteRepository.updateNote(note) }

    fun getAllNotes(): LiveData<List<Note>> = liveData {
        emitSource(noteRepository.getAllNotes())
    }

    fun searchNote(query: String?): LiveData<List<Note>> =
        liveData {
            val searchedNotes = noteRepository.searchNote(query)
            emitSource(searchedNotes)  // Emit the retrieved LiveData from the repository
        }


}