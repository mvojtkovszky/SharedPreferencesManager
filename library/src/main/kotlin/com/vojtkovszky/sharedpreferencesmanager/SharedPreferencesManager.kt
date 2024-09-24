@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.vojtkovszky.sharedpreferencesmanager

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.Exception
import kotlin.concurrent.thread

/**
 * Class handling persistence of data using [SharedPreferences] .
 *
 * All put methods will automatically call [SharedPreferences.edit] concluded with
 * [SharedPreferences.apply].
 *
 * All known objects or list of objects are cached in memory to avoid redundant deserialization:
 * - Edit to object or list of objects will update the cache.
 * - Retrieving object or list of objects will retrieve the cached copy if it exists.
 * - for other functions, [SharedPreferences] uses in-memory caching for known values.
 *
 * @param sharedPreferences provide your implementation of [SharedPreferences].
 * Usually it would be [Context.getSharedPreferences].
 * If persisting data in file is not desired, provide [InMemorySharedPreferences].
 * @param json Provide your own [Json] instance used for serialization of custom objects. Defaults to [Json.Default]
 * @param errorListener any exceptions thrown while parsing will be invoked using this listener
 */
class SharedPreferencesManager(
    val sharedPreferences: SharedPreferences,
    val json: Json = Json,
    val errorListener: ((e: Exception) -> Unit)? = null
) {

    @PublishedApi
    internal val cachedObjects: MutableMap<String, Any> = mutableMapOf()
    @PublishedApi
    internal val cachedLists: MutableMap<String, List<Any>> = mutableMapOf()

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
    fun setBoolean(key: String, value: Boolean) {
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
    fun setFloat(key: String, value: Float) {
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
    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    // endregion Int

    // region List
    /**
     * Retrieve [List] from [SharedPreferences], stored as serialized json string.
     * Note that [T] has to be annotated as [Serializable] or result will be null due to
     * caught [SerializationException] while decoding [List] from [String].
     *
     * Note that this method may include json deserialization, especially when fetching the existing
     * value for the first time, which might block the UI thread. If that is the issue, consider
     * using [getListAsync] instead.
     */
    inline fun <reified T: Any> getList(key: String, defaultValue: List<T>? = null): List<T>? {
        // return cached if exists so we don't have to repeat serializing
        if (cachedLists.containsKey(key)) {
            cachedLists[key].let {
                // this casting might fail if we try and write two different list types under
                // the same key. Invoke error in this case
                return try {
                    @Suppress("UNCHECKED_CAST")
                    it as List<T>?
                } catch (e: Exception) {
                    errorListener?.invoke(e)
                    defaultValue
                }
            }
        }

        return getString(key, null)?.let {
            return try {
                json.decodeFromString<List<T>>(it).also { decodedList ->
                    // cache the decoded object
                    cachedLists[key] = decodedList
                }
            } catch (e: Exception) {
                errorListener?.invoke(e)
                defaultValue
            }
        } ?: defaultValue
    }

    /**
     * Same as [getList] but result is reported via callback and will not block the UI thread,
     * which is a potential issue when reading a large object for the first time, as deserialization
     * process will need to take place.
     */
    inline fun <reified T: Any> getListAsync(
        key: String, defaultValue: List<T>? = null, crossinline callback: (List<T>?) -> Unit
    ) {
        thread(start = true) {
            callback.invoke(getList(key, defaultValue))
        }
    }

    /**
     * Put [List] into [SharedPreferences] as serialized json string and apply change.
     * Note that [T] has to be annotated as [Serializable] or key will be null-ed due to
     * caught [SerializationException] while  encoding [List] to [String].
     */
    inline fun <reified T: Any> setList(key: String, list: List<T>?) {
        // update cache immediately
        if (list != null) {
            cachedLists[key] = list
        } else {
            cachedLists.remove(key)
        }

        // serialize and apply to disk in the background
        val serialized = try {
            if (list == null) null
            else json.encodeToString(list)
        } catch (e: Exception) {
            errorListener?.invoke(e)
            null
        }
        setString(key, serialized)
    }

    /**
     * Calls [setList] in a background thread and invokes [callback] when complete
     */
    inline fun <reified T: Any> setListAsync(key: String, list: List<T>?, crossinline callback: () -> Unit) {
        thread(start = true) {
            setList(key, list)
            callback.invoke()
        }
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
    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    // endregion Long

    // region Object
    /**
     * Retrieve an object [T] from [SharedPreferences], stored as serialized json string.
     * Note that [T] has to be annotated as [Serializable] or result will be null due to
     * caught [SerializationException] while decoding [T] from [String].
     *
     * Note that this method may include json deserialization, especially when fetching the existing
     * value for the first time, which might block the UI thread. If that is the issue, consider
     * using [getObjectAsync] instead.
     */
    inline fun <reified T: Any> getObject(key: String, defaultValue: T? = null) : T? {
        // return cached if exists so we don't have to repeat serializing
        if (cachedObjects.containsKey(key)) {
            // this casting might fail if we try and write two different list types under
            // the same key. Invoke error in this case
            cachedObjects[key].let {
                return try {
                    it as T?
                } catch (e: Exception) {
                    errorListener?.invoke(e)
                    defaultValue
                }
            }
        }

        return getString(key, null)?.let {
            return try {
                json.decodeFromString<T>(it).also { decodedObject ->
                    // cache the decoded object
                    cachedObjects[key] = decodedObject
                }
            } catch (e: Exception) {
                errorListener?.invoke(e)
                defaultValue
            }
        } ?: defaultValue
    }

    /**
     * Same as [getObject] but result is reported via callback and will not block the UI thread,
     * which is a potential issue when reading a large object for the first time, as deserialization
     * process will need to take place.
     */
    inline fun <reified T: Any> getObjectAsync(
        key: String, defaultValue: T? = null, crossinline callback: (T?) -> Unit
    ) {
        thread(start = true) {
            callback.invoke(getObject(key, defaultValue))
        }
    }

    /**
     * Put an object [T] into [SharedPreferences] as serialized json string and apply the change.
     * Note that [T] has to be annotated as [Serializable] or key will be null-ed due to
     * caught [SerializationException] while encoding [T] to [String].
     */
    inline fun <reified T: Any> setObject(key: String, obj: T?) {
        // update cache immediately
        if (obj != null) {
            cachedObjects[key] = obj
        } else {
            cachedObjects.remove(key)
        }

        val serialized = try {
            if (obj == null) null
            else json.encodeToString(obj)
        } catch (e: Exception) {
            errorListener?.invoke(e)
            null
        }

        setString(key, serialized).also {
            // cache when setting
            if (obj != null) {
                cachedObjects[key] = obj
            }
        }
    }

    /**
     * Calls [setObject] in a background thread and invokes [callback] when complete
     */
    inline fun <reified T: Any> setObjectAsync(key: String, obj: T?, crossinline callback: () -> Unit) {
        thread(start = true) {
            setObject(key, obj)
            callback.invoke()
        }
    }
    // endregion Object

    // region String
    /**
     * Will return [SharedPreferences.getString]
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Put [String] into [SharedPreferences] and apply change
     */
    fun setString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    // endregion String

    // region StringSet
    /**
     * Will return [SharedPreferences.getStringSet]
     */
    fun getStringSet(key: String, defaultValue: MutableSet<String>? = null): MutableSet<String>? {
        return sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue
    }

    /**
     * Put [MutableSet] of [String] into [SharedPreferences] and apply change
     */
    fun setStringSet(key: String, value: MutableSet<String>?) {
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
