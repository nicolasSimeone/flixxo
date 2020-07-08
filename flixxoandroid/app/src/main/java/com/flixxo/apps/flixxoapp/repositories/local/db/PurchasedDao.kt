package com.flixxo.apps.flixxoapp.repositories.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.flixxo.apps.flixxoapp.model.ContentPurchased

@Dao
interface PurchasedDao {

    @Query("SELECT id, uuid, userName FROM contentpurchased WHERE username = :username")
    fun getContentByUser(username: String): List<ContentPurchased>

    @Insert
    fun insertContent(purchased: ContentPurchased)

    @Delete
    fun deleteContent(username: ContentPurchased)

}