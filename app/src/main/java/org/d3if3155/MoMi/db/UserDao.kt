package org.d3if3155.MoMi.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert
    fun insert(user: UserEntity)

    @Update
    fun update(user: UserEntity)

    @Delete
    fun delete(user: UserEntity)

    // latest inserted user
    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    fun getLatestUser(): LiveData<UserEntity>

    // get transaction from 1 user by id
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Long): LiveData<UserEntity>

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUser(): LiveData<List<UserEntity>>

    @Query("DELETE FROM users")
    fun clearData()
}