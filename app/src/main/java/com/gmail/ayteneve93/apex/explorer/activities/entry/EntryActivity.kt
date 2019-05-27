package com.gmail.ayteneve93.apex.explorer.activities.entry

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.gmail.ayteneve93.apex.explorer.R
import com.gmail.ayteneve93.apex.explorer.activities.intro.IntroActivity
import com.gmail.ayteneve93.apex.explorer.utils.ApplicationPreference
import com.gmail.ayteneve93.apex.explorer.utils.MainAuthenticationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class EntryActivity : MainAuthenticationManager.MainAuthenticationManageableActivity() {
    private val mApplicationPreference : ApplicationPreference by lazy { ApplicationPreference.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Entry Activity에서 런칭 후 일반 Installation 런칭의 경우 Intro로 넘어가고 아니면
         * 다른 Dynamic 모듈을 바로 호출. URI 프리픽스 제한이 1달 후에 해제됨. 6/24일에 다시 봐야할듯
         */
        val launchedAsInstantApp : Boolean = false // <- 나중에 이 부분만 새롭게 수정 일단 무조건 설치 => 실행으로 판단
        if(!launchedAsInstantApp) {

        } else {
            if(mApplicationPreference.getAppConfigUseIntroAnim()) startActivity(Intent(this, IntroActivity::class.java))
            else signIn()
        }
    }
    private fun signIn() {
        val currentUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if(currentUser == null) {
            super.requestSignIn {
                isSuccess : Boolean, fireBaseUser : FirebaseUser? ->
                if (isSuccess) {

                } else {
                    Toast.makeText(this, R.string.retry_signin, Toast.LENGTH_LONG).show()
                    signIn()
                }
            }
        } else {

        }
    }
}


