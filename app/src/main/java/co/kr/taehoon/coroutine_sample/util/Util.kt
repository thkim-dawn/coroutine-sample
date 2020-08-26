package co.kr.taehoon.coroutine_sample.util

import androidx.lifecycle.MutableLiveData
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

inline fun <reified T> JsonElement.parsingData(key: String): T = let {
    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd")
        .registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date?> {
            var df: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            @Throws(JsonParseException::class)
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Date? {
                return try {
                    df.parse(json.asString)
                } catch (e: ParseException) {
                    null
                }
            }
        })
        .serializeNulls()
        .create()

    gson.run {
        val jsonObject = if (key.isEmpty()) {
            this@parsingData
        } else {
            it.asJsonObject.get(key)
        }
        fromJson(jsonObject, object : TypeToken<T>() {}.type)
    }
}