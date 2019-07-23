package com.ayteneve93.apexexplorer.data.managers

import com.ayteneve93.apexexplorer.data.UserAccountInfoModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class UserAccountInfoModelManager {

    private val mUserAccountInfoModel : UserAccountInfoModel = UserAccountInfoModel(false, null, null)
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