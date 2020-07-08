package com.flixxo.apps.flixxoapp.repositories.local.db

import androidx.room.Dao
import androidx.room.Query
import com.flixxo.apps.flixxoapp.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

}