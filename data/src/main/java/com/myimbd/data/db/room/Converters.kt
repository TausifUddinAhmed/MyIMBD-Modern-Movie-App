package com.myimbd.data.db.room



import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters(
    private val gson: Gson = Gson()
) {
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return gson.fromJson(json, listType)
    }
}
