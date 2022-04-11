package com.binar.mynote.room

import androidx.room.*
import com.binar.mynote.data.Note

@Dao
interface NoteDao {
    @Insert
     fun addNote(note: Note):Long

    @Update
    fun updateNote(note: Note):Int

    @Delete
     fun deleteNote(note: Note):Int

    @Query("SELECT * FROM note ORDER BY id DESC")
     fun getNote(): List<Note>
}