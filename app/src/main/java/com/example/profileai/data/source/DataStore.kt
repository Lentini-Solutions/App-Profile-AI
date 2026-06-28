package com.example.profileai.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cursosant.profileaiant.Constants
import com.example.profileai.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DS_PREFERENCES_PROFILE
)

class ProfileDataStore(private val context: Context){

    private object PreferencesKeys {
        val NAME = stringPreferencesKey(name = Constants.P_NAME)
        val EMAIL = stringPreferencesKey(name = Constants.P_EMAIL)
        val WEBSITE = stringPreferencesKey(name = Constants.P_WEBSITE)
        val PHONE = stringPreferencesKey(name = Constants.P_IMAGE)
        val IMAGE = stringPreferencesKey(name = Constants.P_PHONE)
        val LATITUDE = stringPreferencesKey(name = Constants.P_LATITUDE)
        val LONGITUDE = stringPreferencesKey(name = Constants.P_LONGITUDE)

        val ENABLE_CLICKS = booleanPreferencesKey(name = Constants.DS_ENABLE_CLICKS)
        val IMG_SIZES = stringPreferencesKey(name = Constants.DS_IMG_SIZE)
    }

    val user: Flow<User> = context.dataStore.data.map { prefs ->
        User(
            name = prefs[PreferencesKeys.NAME] ?: "",
            email = prefs[PreferencesKeys.EMAIL] ?: "",
            phone = prefs[PreferencesKeys.PHONE] ?: "",
            website = prefs[PreferencesKeys.WEBSITE] ?: "",
            image = prefs[PreferencesKeys.IMAGE] ?: "",
            latitude = prefs[PreferencesKeys.LATITUDE] ?: "",
            longitude = prefs[PreferencesKeys.LONGITUDE] ?: "",
        )
    }

    val areClicksEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.ENABLE_CLICKS] ?: true
    }

    val getImageSize: Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.IMG_SIZES] ?: Constants.DS_SIZE_LARGE
    }

    suspend fun saveOrUpdateUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NAME] = user.name
            prefs[PreferencesKeys.EMAIL] = user.email
            prefs[PreferencesKeys.WEBSITE] = user.website
            prefs[PreferencesKeys.IMAGE] = user.image
            prefs[PreferencesKeys.LONGITUDE] = user.latitude
            prefs[PreferencesKeys.LATITUDE] = user.longitude
            prefs[PreferencesKeys.PHONE] = user.phone
        }
    }

    suspend fun saveEnabledClicks(enabled: Boolean){
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ENABLE_CLICKS] = enabled
        }
    }

    suspend fun saveSizeImage(sizeImage: String){
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.IMG_SIZES] = sizeImage
        }
    }

    suspend fun clearData() {
        context.dataStore.edit { prefs ->
           prefs.clear()
        }
    }

}