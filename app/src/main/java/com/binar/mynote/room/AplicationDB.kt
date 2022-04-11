package com.binar.mynote.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.binar.mynote.data.Note
import com.binar.mynote.data.User

@Database(entities = [Note::class,User::class], version = 1)
abstract class AplicationDB: RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao

    companion object{
        private var instance : AplicationDB?=null
        fun getInstance(context: Context):AplicationDB?{
            if (instance==null){
                synchronized(AplicationDB::class){
                    instance= Room.databaseBuilder(context.applicationContext,AplicationDB::class.java,"MyNote.db").build()
                }
            }
            return instance
        }
        fun destroyInstance(){
            instance= null
        }
    }
}