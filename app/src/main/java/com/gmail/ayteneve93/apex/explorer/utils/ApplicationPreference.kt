package com.gmail.ayteneve93.apex.explorer.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.lang.IllegalStateException


class ApplicationPreference private constructor(private val application: Application){

    private val APP_CONFIG : SharedPreferences by lazy { application.getSharedPreferences(APP_CONFIG_NAME, Context.MODE_PRIVATE) }
    private val APP_CONFIG_NAME : String by lazy { "${ApplicationPreference::class.java.simpleName}'$'APP_CONFIG" }
        private val APP_CONFIG_USE_INTRO_ANIM_NAME : String = "$APP_CONFIG_NAME'$'APP_CONFIG_USER_INTRO_ANIM"
            val getAppConfigUseIntroAnim = { APP_CONFIG.getBoolean(APP_CONFIG_USE_INTRO_ANIM_NAME, true) }
            val setAppConfigUseIntroAnim = { appConfigUseIntroAnim : Boolean -> APP_CONFIG.edit().putBoolean(APP_CONFIG_USE_INTRO_ANIM_NAME, appConfigUseIntroAnim).apply()}

    companion object {
        private var instance : ApplicationPreference? = null
        fun getInstance() : ApplicationPreference {
            return when(instance) {
                null -> throw IllegalStateException("${ApplicationPreference::class.java.simpleName} is Not Initialized")
                else -> instance!!
            }
        }
        val initialize = {
            application : Application ->
            instance = when(instance) {
                null -> ApplicationPreference(application)
                else -> throw IllegalStateException("${ApplicationPreference::class.java.simpleName} is Already Initialized")
            }
        }

    }
}