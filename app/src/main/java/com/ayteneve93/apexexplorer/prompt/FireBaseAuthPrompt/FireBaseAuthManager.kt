package com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.ayteneve93.apexexplorer.prompt.PromptActivity
import com.ayteneve93.apexexplorer.prompt.PromptIntentAction
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Single

class FireBaseAuthManager {

    private var onAuthenticationResult : ((Boolean, GoogleSignInAccount?) -> Unit)? = null
    fun requestGoogleFireBaseAuthentication(sourceActivity : AppCompatActivity, onAuthenticationResult : (isSucceed : Boolean, account : GoogleSignInAccount?) -> Unit) {
        val requestGoogleFireBaseIntent = Intent(sourceActivity, PromptActivity::class.java)
            .setAction(PromptIntentAction.FIRE_BASE_AUTH)
            .putExtra(FireBaseAuthIntentPreference.RequestDomain.KEY, FireBaseAuthIntentPreference.RequestDomain.Values.GOOGLE)
        sourceActivity.startActivity(requestGoogleFireBaseIntent)
        this.onAuthenticationResult = onAuthenticationResult
    }

    fun onRequestGoogleFireBaseAuthentication(googleSignInResult: GoogleSignInResult) {
        when(googleSignInResult.isSuccess) {
            true -> {
                val account = googleSignInResult.signInAccount
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                onAuthenticationResult?.let { it(true, account) }
            }
            false -> onAuthenticationResult?.let { it(false, null) }
        }
    }

}