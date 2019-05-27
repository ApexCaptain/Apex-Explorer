package com.gmail.ayteneve93.apex.explorer.activities.intro

import android.animation.Animator
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.gmail.ayteneve93.apex.explorer.R
import com.gmail.ayteneve93.apex.explorer.utils.MainAuthenticationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_intro.*

private const val INTRO_LOTTIE_ANIMATION_JSON = "intro_lottie_animation.json"

class IntroActivity : MainAuthenticationManager.MainAuthenticationManageableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Activity as Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Hide Action Bar
        supportActionBar?.hide()

        // Link With Layout Resource
        setContentView(R.layout.activity_intro)

        // Set and Play Intro Animation
        intro_lottie_animation_view.setAnimation(INTRO_LOTTIE_ANIMATION_JSON)
        intro_lottie_animation_view.playAnimation()
        intro_lottie_animation_view.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    signIn()
                }
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            }
        )
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