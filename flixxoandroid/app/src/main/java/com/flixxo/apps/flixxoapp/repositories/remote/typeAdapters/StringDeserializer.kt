package com.flixxo.apps.flixxoapp.repositories.remote.typeAdapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class StringDeserializer : JsonDeserializer<String> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): String {
        json?.let {

            if (json.asJsonPrimitive.isNumber) {
                return json.asJsonPrimitive.toString()
            }

            return json.asJsonPrimitive.asString
        } ?: kotlin.run {
            return ""
        }
    }

}