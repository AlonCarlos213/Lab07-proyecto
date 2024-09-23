package com.example.lab07

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuDao {
    @Insert
    suspend fun insert(menuItem: MenuItem)

    @Query("SELECT * FROM MenuItem")
    suspend fun getAll(): List<MenuItem>

    @Query("DELETE FROM MenuItem WHERE id = (SELECT MAX(id) FROM MenuItem)")
    suspend fun deleteLastMenuItem()
}
