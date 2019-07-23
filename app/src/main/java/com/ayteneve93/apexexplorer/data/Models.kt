package com.ayteneve93.apexexplorer.data

data class UserAccountInfoModel(
    var isAuthenticated : Boolean?,
    var email : String?,
    var name : String?)

data class AppTitleModel(
    val eachCharacter : String)

data class FileModel(
    var icon : String,
    var title : String,
    var isDirectory : Boolean,
    var date : String,
    var isFavorite : Boolean,
    var isHidden : Boolean,
    var canonicalPath : String,
    var size : Float? = null,
    var sizeUnit : String? = null)