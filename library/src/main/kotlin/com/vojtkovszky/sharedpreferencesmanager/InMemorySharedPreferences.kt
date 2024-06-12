@file:Suppress("unused")

package com.vojtkovszky.sharedpreferencesmanager

import android.content.SharedPreferences

/**
 * Implementation of [SharedPreferences] where data is persisted in memory only and omit using files.
 * Useful if you don't need to persist data outside of application lifecycle
 * or if you want to run tests without mocking the context.
 */
class InMemorySharedPreferences: SharedPreferences {

    // acting as repository
    @Volatile
    private var booleansMap: MutableMap<String, Boolean> = mutableMapOf()
    @Volatile
    private var floatsMap: MutableMap<String, Float> = mutableMapOf()
    @Volatile
    private var intsMap: MutableMap<String, Int> = mutableMapOf()
    @Volatile
    private var longMap: MutableMap<String, Long> = mutableMapOf()
    @Volatile
    private var stringsMap: MutableMap<String, String?> = mutableMapOf()
    @Volatile
    private var stringsSetMap: MutableMap<String, MutableSet<String>?> = mutableMapOf()

    // change listeners
    private val changeListeners: MutableList<SharedPreferences.OnSharedPreferenceChangeListener> = mutableListOf()

    override fun getAll(): MutableMap<String, *> {
        val result: MutableMap<String, Any?> = mutableMapOf()
        result.putAll(booleansMap)
        result.putAll(floatsMap)
        result.putAll(intsMap)
        result.putAll(longMap)
        result.putAll(stringsMap)
        result.putAll(stringsSetMap)
        return result
    }

    override fun getString(key: String?, value: String?): String? {
        return stringsMap[key] ?: value
    }

    override fun getStringSet(key: String?, value: MutableSet<String>?): MutableSet<String> {
        return stringsSetMap[key] ?: mutableSetOf()
    }

    override fun getInt(key: String?, value: Int): Int {
        return intsMap[key] ?: value
    }

    override fun getLong(key: String?, value: Long): Long {
        return longMap[key] ?: value
    }

    override fun getFloat(key: String?, value: Float): Float {
        return floatsMap[key] ?: value
    }

    override fun getBoolean(key: String?, value: Boolean): Boolean {
        return booleansMap[key] ?: value
    }

    override fun contains(key: String?): Boolean {
        return all.containsKey(key)
    }

    override fun edit(): SharedPreferences.Editor {
        return InMemoryEditor()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        listener?.let {
            if (!changeListeners.contains(it)) {
                changeListeners.add(it)
            }
        }
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        listener?.let {
            changeListeners.remove(it)
        }
    }

    /**
     * Implementation of editor that doesn't rely on file storage.
     */
    internal inner class InMemoryEditor: SharedPreferences.Editor {

        private val booleansMap: MutableMap<String, Boolean> = this@InMemorySharedPreferences.booleansMap
        private val floatsMap: MutableMap<String, Float> = this@InMemorySharedPreferences.floatsMap
        private val intsMap: MutableMap<String, Int> = this@InMemorySharedPreferences.intsMap
        private val longMap: MutableMap<String, Long> = this@InMemorySharedPreferences.longMap
        private val stringsMap: MutableMap<String, String?> = this@InMemorySharedPreferences.stringsMap
        private val stringsSetMap: MutableMap<String, MutableSet<String>?> = this@InMemorySharedPreferences.stringsSetMap

        private val changedKeys: MutableList<String> = mutableListOf()

        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            key?.let {
                stringsMap[it] = value
                changedKeys.add(it)
            }
            return this
        }

        override fun putStringSet(key: String?, value: MutableSet<String>?): SharedPreferences.Editor {
            key?.let {
                stringsSetMap[it] = value
                changedKeys.add(it)
            }
            return this
        }

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
            key?.let {
                intsMap[it] = value
                changedKeys.add(it)
            }
            return this
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
            key?.let {
                longMap[it] = value
                changedKeys.add(it)
            }
            return this
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
            key?.let {
                floatsMap[it] = value
                changedKeys.add(it)
            }
            return this
        }

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
            key?.let {
                booleansMap[it] = value
                changedKeys.add(it)
            }
            return this
        }

        override fun remove(key: String?): SharedPreferences.Editor {
            key?.let {
                // keep track if key actually removed
                var keyRemoved = false

                booleansMap.remove(it).also { value -> keyRemoved = keyRemoved || value != null }
                floatsMap.remove(it).also { value -> keyRemoved = keyRemoved || value != null }
                intsMap.remove(it).also { value -> keyRemoved = keyRemoved || value != null }
                longMap.remove(it).also { value -> keyRemoved = keyRemoved || value != null }
                stringsMap.remove(it).also { value -> keyRemoved = keyRemoved || value != null }
                stringsSetMap.remove(it).also { value -> keyRemoved = keyRemoved || value != null }

                // only added to changed if removed
                if (keyRemoved) {
                    changedKeys.add(it)
                }
            }
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            for (bool in booleansMap) { changedKeys.add(bool.key) }
            for (float in floatsMap) { changedKeys.add(float.key) }
            for (int in intsMap) { changedKeys.add(int.key) }
            for (long in longMap) { changedKeys.add(long.key) }
            for (string in stringsMap) { changedKeys.add(string.key) }
            for (stringSet in stringsSetMap) { changedKeys.add(stringSet.key) }

            booleansMap.clear()
            floatsMap.clear()
            intsMap.clear()
            longMap.clear()
            stringsMap.clear()
            stringsSetMap.clear()

            return this
        }

        override fun commit(): Boolean {
            // since we don't deal with file storage, this is completely identical to apply()
            apply()
            return true
        }

        override fun apply() {
            this@InMemorySharedPreferences.booleansMap = this@InMemoryEditor.booleansMap
            this@InMemorySharedPreferences.floatsMap = this@InMemoryEditor.floatsMap
            this@InMemorySharedPreferences.intsMap = this@InMemoryEditor.intsMap
            this@InMemorySharedPreferences.longMap = this@InMemoryEditor.longMap
            this@InMemorySharedPreferences.stringsMap = this@InMemoryEditor.stringsMap
            this@InMemorySharedPreferences.stringsSetMap = this@InMemoryEditor.stringsSetMap

            // invoke change listeners
            for (listener in changeListeners) {
                for (changedKey in changedKeys) {
                    listener.onSharedPreferenceChanged(this@InMemorySharedPreferences, changedKey)
                }
            }
            changeListeners.clear()
        }
    }
}