package com.ayteneve93.apexexplorer.data

import androidx.databinding.ObservableField

data class UserAccountInfoModel(
    var isAuthenticated : Boolean?,
    var email : String?,
    var name : String?)

data class AppTitleModel(
    val eachCharacter : String)

data class FileModel(
    var iconResId : Int,
    var title : String,
    var isDirectory : Boolean,
    var date : String,
    var isHidden : Boolean,
    var canonicalPath : String,
    var parentDirectoryPath : String? = null,
    var isFavorite : ObservableField<Boolean> = ObservableField(false),
    var extension : String? = null,
    var originalSize : Long? = null,
    var size : Float? = null,
    var sizeUnit : String? = null)