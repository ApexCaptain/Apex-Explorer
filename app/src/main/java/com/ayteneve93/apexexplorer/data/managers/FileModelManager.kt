package com.ayteneve93.apexexplorer.data.managers

import android.app.Application
import android.os.Environment
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
    fun scanFileListFrom(path : String, onScanResult : (isSucceed : Boolean, fileModelList : ArrayList<FileModel>?) -> Unit) {
        TedPermission.with(application)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    val file = File(Environment.getExternalStorageDirectory().path + "/Download")
                    if(file.exists()) {
                        if (file.isDirectory) {
                            val fileModelList = ArrayList<FileModel>()
                            file.listFiles().forEach {
                                eachFile ->
                                if(eachFile.exists()) {
                                    val eachFileModel = FileModel (
                                            getFileIcon(eachFile),
                                            eachFile.name,
                                            eachFile.isDirectory,
                                            SimpleDateFormat.getDateTimeInstance().format(Date(eachFile.lastModified())),
                                            getIsFavorite(eachFile),
                                            eachFile.isHidden,
                                            eachFile.canonicalPath)
                                    if(!eachFileModel.isDirectory) setFileSizeAndUnit(eachFile, eachFileModel)
                                    fileModelList.add(eachFileModel)
                                }
                            }
                            if(fileModelList.size != 0) onScanResult(true, fileModelList)
                            else onScanResult(false, null)
                        } else onScanResult(false, null)
                    }
                }
                override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
                }
            })
            .setRationaleMessage("권한 필요함")
            .setDeniedMessage("거부 ㄴㄴ")
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    private fun getFileIcon(file : File) : String {
        return "아직 개발중"
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
            size = dataSize.toFloat()
            sizeUnit = application.resources.getStringArray(R.array.file_size_unit)[parsedCount]
        }
        return
    }

    private fun getIsFavorite(file : File) : Boolean {
        return false
    }
}