package com.ayteneve93.apexexplorer.data

data class UserAccountInfoModel(var isAuthenticated : Boolean?, var email : String?, var name : String?)
data class AppTitleModel(val eachCharacter : String)
data class FileModel(var icon : String, var title : String, var size : Float, var sizeUnit : Int, var date : String, var isFavorite : Boolean)