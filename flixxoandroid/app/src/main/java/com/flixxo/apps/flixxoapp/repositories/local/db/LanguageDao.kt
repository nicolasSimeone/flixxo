package com.flixxo.apps.flixxoapp.repositories.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.flixxo.apps.flixxoapp.model.Language

@Dao
interface LanguageDao {
    @Query("SELECT * FROM language")
    fun getLanguages(): List<Language>

    @Insert
    fun insertLanguages(language: List<Language>)

    @Query("SELECT nameNative FROM language WHERE lang = :langId")
    fun getAudioLang(langId: String): String
}