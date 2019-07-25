package com.ayteneve93.apexexplorer.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

private const val USER_PREF_KEY = "utils#User#KEY"
class PreferenceUtils(
    application: Application
) {
    private val userPreference : SharedPreferences = application.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)

    fun setBooleanUserPreference(attribute : PreferenceCategory.User, insertValue : Boolean) : PreferenceUtils {
        userPreference.edit().putBoolean(attribute.attributeName, insertValue).apply()
        return this
    }
    fun getBooleanUserPreference(attribute : PreferenceCategory.User, default : Boolean) : Boolean {
        return userPreference.getBoolean(attribute.attributeName, default)
    }
    fun getStringUserPreference(attribute: PreferenceCategory.User, default : String?) : String? {
        return userPreference.getString(attribute.attributeName, default)
    }
    fun setStringUserPreference(attribute: PreferenceCategory.User, insertValue : String) : PreferenceUtils {
        userPreference.edit().putString(attribute.attributeName, insertValue).apply()
        return this
    }
    fun addStringUserPreferenceSet(attribute: PreferenceCategory.User, insertValue: String) : PreferenceUtils {
        val storedStringSet = getStringUserPreferenceSet(attribute)
        storedStringSet.add(insertValue)
        userPreference.edit().putString(attribute.attributeName, storedStringSet.toString()).apply()
        return this
    }
    fun removeStringUserPreferenceSet(attribute: PreferenceCategory.User, removeValue : String) : PreferenceUtils {
        val storedStringSet = getStringUserPreferenceSet(attribute)
        storedStringSet.remove(removeValue)
        userPreference.edit().putString(attribute.attributeName, storedStringSet.toString()).apply()
        return this
    }
    fun getStringUserPreferenceSet(attribute : PreferenceCategory.User) : HashSet<String> {
        val storedStringSet = HashSet<String>()
        userPreference.getString(attribute.attributeName, null)?.let {
            it.removeSurrounding("[", "]").split(", ").forEach {
                eachString ->
                storedStringSet.add(eachString)
            }
        }
        return storedStringSet
    }
    fun checkContainsStringUserPreferenceSet(attribute: PreferenceCategory.User, checkValue : String) : Boolean {
        val storedStringSet = getStringUserPreferenceSet(attribute)
        return storedStringSet.contains(checkValue)
    }
}

object PreferenceCategory {
    enum class User(val attributeName : String) {
        USE_FINGERPRINT_AUTHENTICATION("utils#PreferenceCategory#User#Attributes#USE_FINGERPRINT_AUTHENTICATION"),
        SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION("utils#PreferenceCategory#User#Attributes#SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION"),
        LAST_VIEWED_FOLDER_NAME("utils#PreferenceCategory#User#Attributes#LAST_VIEWED_FOLDER_NAME"),
        FAVORITE_FILES("utils#PreferenceCategory#User#Attributes#FAVORITE_FILES")
    }
}
