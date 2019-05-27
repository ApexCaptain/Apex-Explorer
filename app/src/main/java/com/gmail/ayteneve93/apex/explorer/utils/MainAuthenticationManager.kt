package com.gmail.ayteneve93.apex.explorer.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.gmail.ayteneve93.apex.explorer.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

private const val REQUEST_CODE_GOOGLE_SIGN_IN = 900
class MainAuthenticationManager private constructor(){
    abstract class MainAuthenticationManageableActivity : AppCompatActivity() {
        private val fireBaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
        lateinit var googleSignInCallback : (Boolean, FirebaseUser?) -> Unit
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            when(requestCode) {
                REQUEST_CODE_GOOGLE_SIGN_IN -> {
                    val result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                    if(result.isSuccess) {
                        val googleSignInAccount : GoogleSignInAccount? = result.signInAccount
                        if(googleSignInAccount != null) {
                            fireBaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)).addOnCompleteListener {
                                authResultTask : Task<AuthResult> ->
                                val isSuccess : Boolean = authResultTask.isSuccessful
                                if(isSuccess) googleSignInCallback.invoke(isSuccess, fireBaseAuth.currentUser)
                                else googleSignInCallback.invoke(isSuccess, null)
                            }
                        } else requestSignIn(googleSignInCallback)
                    } else requestSignIn(googleSignInCallback)
                }
            }
        }
        protected fun requestSignIn(signInCallback : (isSuccess : Boolean, fireBaseUser : FirebaseUser?) -> Unit){
            requestSignIn(this, signInCallback)
        }
    }
    companion object {
        private fun requestSignIn(callerActivity : MainAuthenticationManageableActivity, signInCallback : (Boolean, FirebaseUser?) -> Unit) {
            GoogleApiClient.Builder(callerActivity)
                .enableAutoManage(callerActivity) {

                }
                .addApi(
                    Auth.GOOGLE_SIGN_IN_API, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(callerActivity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build())
                .build().let {
                    callerActivity.googleSignInCallback = signInCallback
                    callerActivity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(it), REQUEST_CODE_GOOGLE_SIGN_IN)
                }
        }
    }

}

