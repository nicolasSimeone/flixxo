package com.flixxo.apps.flixxoapp.repositories.remote.typeAdapters

import com.flixxo.apps.flixxoapp.model.Author
import com.flixxo.apps.flixxoapp.model.Profile
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class AuthorDeserializer : JsonDeserializer<Author> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Author? {
        json?.let {
            if (it.isJsonPrimitive) {
                it.asJsonPrimitive.let { jsonPrimitive ->
                    if (jsonPrimitive.isString) {
                        val profile = Profile(realName = jsonPrimitive.asString)
                        return Author(profile = profile)
                    }
                }
            }

            return Gson().fromJson(json, Author::class.java)
        } ?: kotlin.run {
            return null
        }
    }

}