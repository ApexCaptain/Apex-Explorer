package com.gmail.ayteneve93.apex.explorer.utils

import android.app.Application
import java.lang.IllegalStateException

class Preference {

    private var application : Application? = null

    private fun initInstance() {

    }

    companion object {
        private val instance : Preference = Preference()
        fun getInstance() : Preference {
            return when(instance.application) {
                null -> throw IllegalStateException("${Preference::class.java} is Not Initialized")
                else -> instance
            }
        }
        val initialize  = {
            application : Application ->
            instance.application = when(instance.application) {
                null -> application
                else -> throw IllegalStateException("${Preference::class.java} is Already Initialized")
            }.also {
                instance.initInstance()
            }
        }
    }
}