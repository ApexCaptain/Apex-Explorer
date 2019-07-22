package com.ayteneve93.apexexplorer.data

import android.content.Context
import com.ayteneve93.apexexplorer.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class DataModelManager{

    fun getAppTitleModel(context : Context) : List<AppTitleModel> {
        val appTitle = context.getString(R.string.app_name)
        val appTitleModelList : ArrayList<AppTitleModel> = ArrayList()
        appTitle.forEach {
            appTitleModelList.add(AppTitleModel(it.toString()))
        }
        return appTitleModelList
    }

    private val mUserAccountInfoModel : UserAccountInfoModel = UserAccountInfoModel(null, null)
    fun setUserAccountInfoModel(account : GoogleSignInAccount) : UserAccountInfoModel {
        return mUserAccountInfoModel.apply {
            email = account.email
            name = account.displayName
        }
    }

    fun getUserAccountInfoModel() : UserAccountInfoModel {
        return  mUserAccountInfoModel
    }

}