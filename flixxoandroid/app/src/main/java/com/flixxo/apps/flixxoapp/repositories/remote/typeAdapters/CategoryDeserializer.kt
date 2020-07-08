package com.flixxo.apps.flixxoapp.repositories.remote.typeAdapters

import com.flixxo.apps.flixxoapp.model.Category
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CategoryDeserializer : JsonDeserializer<Category> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Category? {
        json?.let {
            if (it.isJsonPrimitive) {
                it.asJsonPrimitive.let { jsonPrimitive ->
                    if (jsonPrimitive.isString) {
                        return Category(name = jsonPrimitive.asString)
                    }
                }
            }

            return Gson().fromJson(json, Category::class.java)
        } ?: kotlin.run {
            return null
        }
    }

}