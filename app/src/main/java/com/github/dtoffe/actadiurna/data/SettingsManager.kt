package com.github.dtoffe.actadiurna.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.dtoffe.actadiurna.model.SortBy

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PERSIST_SORT = "persist_sort"
        private const val KEY_LAST_SORT = "last_sort"
    }

    var persistSort: Boolean
        get() = prefs.getBoolean(KEY_PERSIST_SORT, false)
        set(value) = prefs.edit { putBoolean(KEY_PERSIST_SORT, value) }

    var lastSort: SortBy
        get() {
            val name = prefs.getString(KEY_LAST_SORT, SortBy.PRIORITY.name)
            return try {
                SortBy.valueOf(name!!)
            } catch (e: Exception) {
                SortBy.PRIORITY
            }
        }
        set(value) = prefs.edit { putString(KEY_LAST_SORT, value.name) }
}
