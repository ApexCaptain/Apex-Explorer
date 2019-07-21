package com.ayteneve93.apexexplorer.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

private const val USER_PREF_KEY = "utils#User#KEY"
class ApplicationPreference(
    application: Application
) {
    private val userPreference : SharedPreferences = application.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)

    fun setBooleanUserPreference(attribute : PreferenceCategory.User, insertValue : Boolean) {
        userPreference.edit().putBoolean(attribute.attributeName, insertValue).apply()
    }
    fun getBooleanUserPreference(attribute : PreferenceCategory.User, default : Boolean) : Boolean {
        return userPreference.getBoolean(PreferenceCategory.User.USE_FINGERPRINT_AUTHENTICATION.attributeName, default)
    }
}

object PreferenceCategory {
    enum class User(val attributeName : String) {
        USE_FINGERPRINT_AUTHENTICATION("utils#PreferenceCategory#User#Attributes#USE_FINGERPRINT_AUTHENTICATION"),
        SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION("utils#PreferenceCategory#User#Attributes#SET_NOT_TO_ASK_ACTIVATE_FINGERPRINT_AUTHENTICATION")
    }
}
