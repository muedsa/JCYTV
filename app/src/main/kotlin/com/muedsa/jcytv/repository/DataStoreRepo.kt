package com.muedsa.jcytv.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

private const val PREFS_NAME = "setting"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

class DataStoreRepo @Inject constructor(private val context: Context) {
    val dataStore: DataStore<Preferences> = context.dataStore
}