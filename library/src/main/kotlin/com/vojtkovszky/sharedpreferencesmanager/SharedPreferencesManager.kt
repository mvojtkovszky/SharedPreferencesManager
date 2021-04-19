@file:Suppress("unused")

package com.vojtkovszky.sharedpreferencesmanager

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.Exception

/**
 * Class handling persistence of data using [SharedPreferences] .
 *
 * All put methods will automatically call [SharedPreferences.edit] concluded with
 * [SharedPreferences.apply].
 *
 * @param context Required to retrieve shared SharedPreferences.
 * @param fileKey Desired preferences file. Defaults to "app_prefs"
 * @param operatingMode Operating mode of preferences file, defaults to [Context.MODE_PRIVATE]
 * @param json Provide your own [Json] instance. Default is "Json { ignoreUnknownKeys = true }"
 * @param errorListener any exceptions thrown while parsing will be invoked using this listener
 */
class SharedPreferencesManager(
    context: Context,
    fileKey: String = DEFAULT_FILE_KEY,
    operatingMode: Int = DEFAULT_MODE,
    val json: Json = Json,
    val errorListener: ((e: Exception) -> Unit)? = null
) {

    companion object {
        const val DEFAULT_FILE_KEY = "app_prefs"
        const val DEFAULT_MODE = Context.MODE_PRIVATE
    }

    private val sharedPreferences = context.applicationContext.getSharedPreferences(fileKey, operatingMode)

    // region Boolean
    /**
     * Will return [SharedPreferences.getBoolean]
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Put [Boolean] into [SharedPreferences] and apply change
     */
    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    // endregion Boolean

    // region Float
    /**
     * Will return [SharedPreferences.getFloat]
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    /**
     * Put [Float] into [SharedPreferences] and apply change
     */
    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }
    // endregion Float

    // region Int
    /**
     * Will return [SharedPreferences.getInt]
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * Put [Int] into [SharedPreferences] and apply change
     */
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    // endregion Int

    // region List
    /**
     * Retrieve [List] from [SharedPreferences], stored as serialized json string.
     * Note that [T] has to be annotated as [Serializable] or result will be null due to
     * caught [SerializationException] while decoding [List] from [String].
     */
    inline fun <reified T: Any> getList(key: String, defaultValue: List<T>? = null): List<T>? {
        return getString(key, null)?.let {
            return try {
                json.decodeFromString<List<T>>(it)
            } catch (e: Exception) {
                errorListener?.invoke(e)
                defaultValue
            }
        } ?: defaultValue
    }

    /**
     * Put [List] into [SharedPreferences] as serialized json string and apply change.
     * Note that [T] has to be annotated as [Serializable] or key will be null-ed due to
     * caught [SerializationException] while  encoding [List] to [String].
     */
    inline fun <reified T: Any> putList(key: String, list: List<T>?) {
        val serialized = try {
            if (list == null) null
            else json.encodeToString(list)
        } catch (e: Exception) {
            errorListener?.invoke(e)
            null
        }
        putString(key, serialized)
    }
    // endregion List

    // region Long
    /**
     * Will return [SharedPreferences.getLong]
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    /**
     * Put [Long] into [SharedPreferences] and apply change
     */
    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    // endregion Long

    // region Object
    /**
     * Retrieve an object [T] from [SharedPreferences], stored as serialized json string.
     * Note that [T] has to be annotated as [Serializable] or result will be null due to
     * caught [SerializationException] while decoding [T] from [String].
     */
    inline fun <reified T: Any> getObject(key: String, defaultValue: T? = null) : T? {
        return getString(key, null)?.let {
            return try {
                json.decodeFromString<T>(it)
            } catch (e: Exception) {
                errorListener?.invoke(e)
                defaultValue
            }
        } ?: defaultValue
    }

    /**
     * Put an object [T] into [SharedPreferences] as serialized json string and apply the change.
     * Note that [T] has to be annotated as [Serializable] or key will be null-ed due to
     * caught [SerializationException] while encoding [T] to [String].
     */
    inline fun <reified T: Any> putObject(key: String, obj: T?) {
        val serialized = try {
            if (obj == null) null
            else json.encodeToString(obj)
        } catch (e: Exception) {
            errorListener?.invoke(e)
            null
        }
        putString(key, serialized)
    }
    // endregion Object

    // region String
    /**
     * Will return [SharedPreferences.getString]
     */
    fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Put [String] into [SharedPreferences] and apply change
     */
    fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    // endregion String

    // region StringSet
    /**
     * Will return [SharedPreferences.getStringSet]
     */
    fun getStringSet(key: String, defaultValue: MutableSet<String>?): MutableSet<String>? {
        return sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue
    }

    /**
     * Put [MutableSet] of [String] into [SharedPreferences] and apply change
     */
    fun putStringSet(key: String, value: MutableSet<String>?) {
        sharedPreferences.edit().putStringSet(key, value).apply()
    }
    // endregion StringSet

    // region Other
    /**
     * Clears all preferences and apply changes
     */
    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Retrieves all preferences by calling [SharedPreferences.getAll]
     */
    fun getAll(): MutableMap<String, *>? {
        return sharedPreferences.all
    }

    /**
     * Removes a preference with given key and apply change
     */
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
    // endregion Other
}
