package com.ayteneve93.apexexplorer.data.managers

import android.app.Application
import android.os.Environment
import android.util.Log
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.application.SuppressWarningAttributes
import com.ayteneve93.apexexplorer.data.FileModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FileModelManager(private val application : Application) {
    @Suppress(SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
    fun scanFileListFrom(path : String?, onScanResult : (isSucceed : Boolean, fileModelList : ArrayList<FileModel>?) -> Unit) {
        TedPermission.with(application)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    val file = File(path?:Environment.getExternalStorageDirectory().path)
                    if(file.exists()) {
                        if (file.isDirectory) {
                            val fileModelList = ArrayList<FileModel>()
                            file.listFiles().forEach {
                                eachFile ->
                                if(eachFile.exists()) {
                                    val eachFileModel = FileModel (
                                            getFileIconResId(eachFile),
                                            eachFile.name,
                                            eachFile.isDirectory,
                                            SimpleDateFormat.getDateTimeInstance().format(Date(eachFile.lastModified())),
                                            getIsFavorite(eachFile),
                                            eachFile.isHidden,
                                            eachFile.canonicalPath)


                                    if(!eachFileModel.isDirectory) {
                                        eachFileModel.extension = eachFile.extension
                                        setFileSizeAndUnit(eachFile, eachFileModel)
                                    }
                                    fileModelList.add(eachFileModel)
                                }
                            }
                            onScanResult(true, fileModelList)
                        } else onScanResult(false, null)
                    }
                }
                override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {}
            })
            .setRationaleMessage(R.string.permission_external_storage_rational_message)
            .setDeniedMessage(R.string.permission_external_storage_denied_message)
            .setGotoSettingButton(true)
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    private fun getFileIconResId(file : File) : Int {
        return if(file.isDirectory) R.drawable.ic_file_directory
        else {
            when(file.extension) {
                in application.resources.getStringArray(R.array.file_extensions_case_compressed) -> R.drawable.ic_file_compressed
                in application.resources.getStringArray(R.array.file_extensions_case_document) -> R.drawable.ic_file_document
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

    private fun getIsFavorite(file : File) : Boolean {
        return false
    }
}