package com.binar.mynote.room

import androidx.room.*
import com.binar.mynote.data.User

@Dao
interface UserDao {
    @Insert
    fun addUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM user WHERE username=:username LIMIT 1 ")
    fun getUsername(username:String?= null): List<User>

}