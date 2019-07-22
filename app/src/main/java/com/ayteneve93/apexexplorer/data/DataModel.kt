package com.ayteneve93.apexexplorer.data

data class UserAccountInfoModel(var isAuthenticated : Boolean = false, var email : String?, var name : String?)
data class AppTitleModel(val eachCharacter : String)