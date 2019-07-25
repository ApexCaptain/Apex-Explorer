package com.ayteneve93.apexexplorer.data.managers

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.application.SuppressWarningAttributes
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.utils.PreferenceCategory
import com.ayteneve93.apexexplorer.utils.PreferenceUtils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FileModelManager(private val application : Application, private val mPreferenceUtils: PreferenceUtils) {
    fun scanFileListFrom(path : String?, onScanResult : (isSucceed : Boolean, fileModelList : ArrayList<FileModel>?) -> Unit) {
        TedPermission.with(application)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    val file = File(path?:Environment.getExternalStorageDirectory().path)
                    if(file.exists()) {
                        if (file.isDirectory) {
                            val fileModelList = ArrayList<FileModel>()
                            val favoriteFileSet = mPreferenceUtils.getStringUserPreferenceSet(PreferenceCategory.User.FAVORITE_FILES)
                            file.listFiles()?.forEach {
                                eachFile ->
                                if(eachFile.exists() && !eachFile.name.startsWith(".")) { // '.' 으로 시작하는 파일(일반적으로 시스템 파일)은 제외
                                    val eachFileModel = FileModel (
                                            getFileIconResId(eachFile),
                                            eachFile.name,
                                            eachFile.isDirectory,
                                            SimpleDateFormat.getDateTimeInstance().format(Date(eachFile.lastModified())),
                                            eachFile.isHidden,
                                            eachFile.canonicalPath,
                                            file.canonicalPath)
                                    eachFileModel.isFavorite.set(favoriteFileSet.contains(eachFile.canonicalPath))
                                    if(!eachFileModel.isDirectory) {
                                        eachFileModel.extension = eachFile.extension
                                        setFileSizeAndUnit(eachFile, eachFileModel)
                                    }
                                    fileModelList.add(eachFileModel)
                                }
                            }
                            onScanResult(true, fileModelList)
                        } else onScanResult(false, null)
                    } else onScanResult(false, null)
                }
                override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {}
            })
            .setRationaleMessage(R.string.permission_external_storage_rational_message)
            .setDeniedMessage(R.string.permission_external_storage_denied_message)
            .setGotoSettingButton(true)
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    fun getFileInfoFrom(path : String, onGetResult : (isSucceed : Boolean, fileModel : FileModel?) -> Unit) {
        TedPermission.with(application)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    val file = File(path?:Environment.getExternalStorageDirectory().path)
                    if(file.exists()) {
                        val file = File(path)
                        val fileModel = FileModel(
                            getFileIconResId(file),
                            file.name,
                            file.isDirectory,
                            SimpleDateFormat.getDateTimeInstance().format(Date(file.lastModified())),
                            file.isHidden,
                            file.canonicalPath)
                        fileModel.isFavorite.set(true)
                        if(!fileModel.isDirectory) {
                            fileModel.extension = fileModel.extension
                            setFileSizeAndUnit(file, fileModel)
                        }
                        onGetResult(true, fileModel)
                    } else onGetResult(false, null)
                }
                override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {}
            })
            .setRationaleMessage(R.string.permission_external_storage_rational_message)
            .setDeniedMessage(R.string.permission_external_storage_denied_message)
            .setGotoSettingButton(true)
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    fun generateViewIntentFromModel(fileModel : FileModel) : Intent? {
        val fileViewIntent = Intent(Intent.ACTION_VIEW)
        val type : String? = when (fileModel.extension) {
            in application.resources.getStringArray(R.array.file_extensions_case_compressed) -> "application/zip"
            in application.resources.getStringArray(R.array.file_extensions_case_text) -> "text/*"
            in application.resources.getStringArray(R.array.file_extensions_case_excel) -> "application/*"
            in application.resources.getStringArray(R.array.file_extensions_case_image) -> "image/*"
            in application.resources.getStringArray(R.array.file_extensions_case_music) -> "audio/*"
            in application.resources.getStringArray(R.array.file_extensions_case_pdf) -> "application/*"
            in application.resources.getStringArray(R.array.file_extensions_case_video) -> "video/*"
            else -> return null
        }
        fileViewIntent.setDataAndType(Uri.parse(fileModel.canonicalPath), type)
        return fileViewIntent
    }

    private fun getFileIconResId(file : File) : Int {
        return if(file.isDirectory) R.drawable.ic_file_directory
        else {
            when(file.extension) {
                in application.resources.getStringArray(R.array.file_extensions_case_compressed) -> R.drawable.ic_file_compressed
                in application.resources.getStringArray(R.array.file_extensions_case_text) -> R.drawable.ic_file_document
                in application.resources.getStringArray(R.array.file_extensions_case_excel) -> R.drawable.ic_file_excel
                in application.resources.getStringArray(R.array.file_extensions_case_image) -> R.drawable.ic_file_image
                in application.resources.getStringArray(R.array.file_extensions_case_music) -> R.drawable.ic_file_music
                in application.resources.getStringArray(R.array.file_extensions_case_pdf) -> R.drawable.ic_file_pdf
                in application.resources.getStringArray(R.array.file_extensions_case_video) -> R.drawable.ic_file_video
                else -> R.drawable.ic_file_unknown
            }
        }
    }

    private fun setFileSizeAndUnit(file : File, fileModel : FileModel) {
        val splitUnit = 1024
        var dataSize : Double = file.length().toDouble()

        var parsedCount = 0
        while (dataSize > splitUnit) {
            dataSize /= splitUnit
            parsedCount++
        }
        fileModel.apply {
            originalSize = file.length()
            size = dataSize.toFloat()
            sizeUnit = application.resources.getStringArray(R.array.file_size_unit)[parsedCount]
        }
        return
    }

}