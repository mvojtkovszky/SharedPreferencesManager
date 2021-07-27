@file:Suppress("unused")

package com.vojtkovszky.sharedpreferencesmanager

import android.content.SharedPreferences

/**
 * Implementation of [SharedPreferences] where data is persisted in memory only and omit using files.
 * Useful if you need to run tests without mocking the context or if you simply don't need to persist
 * data outside of application lifecycle.
 */
class InMemorySharedPreferences: SharedPreferences {

    // acting as repository
    private var booleansMap: MutableMap<String, Boolean> = mutableMapOf()
    private var floatsMap: MutableMap<String, Float> = mutableMapOf()
    private var intsMap: MutableMap<String, Int> = mutableMapOf()
    private var longMap: MutableMap<String, Long> = mutableMapOf()
    private var stringsMap: MutableMap<String, String?> = mutableMapOf()
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
        return LocalEditor()
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
    inner class LocalEditor: SharedPreferences.Editor {

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
                booleansMap.remove(it)
                floatsMap.remove(it)
                intsMap.remove(it)
                longMap.remove(it)
                stringsMap.remove(it)
                stringsSetMap.remove(it)

                changedKeys.add(it)
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
            apply()
            return true
        }

        override fun apply() {
            this@InMemorySharedPreferences.booleansMap = this@LocalEditor.booleansMap
            this@InMemorySharedPreferences.floatsMap = this@LocalEditor.floatsMap
            this@InMemorySharedPreferences.intsMap = this@LocalEditor.intsMap
            this@InMemorySharedPreferences.longMap = this@LocalEditor.longMap
            this@InMemorySharedPreferences.stringsMap = this@LocalEditor.stringsMap
            this@InMemorySharedPreferences.stringsSetMap = this@LocalEditor.stringsSetMap

            // distribute change listeners
            for (listener in changeListeners) {
                for (changedKey in changedKeys) {
                    listener.onSharedPreferenceChanged(this@InMemorySharedPreferences, changedKey)
                }
            }
            changeListeners.clear()
        }

    }
}