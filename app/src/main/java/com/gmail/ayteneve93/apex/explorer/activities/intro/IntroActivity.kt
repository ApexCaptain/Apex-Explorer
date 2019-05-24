package com.gmail.ayteneve93.apex.explorer.activities.intro

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.gmail.ayteneve93.apex.explorer.R
import kotlinx.android.synthetic.main.activity_intro.*

private const val INTRO_LOTTIE_ANIMATION_JSON = "intro_lottie_animation.json"

class IntroActivity : AppCompatActivity() {

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
                    startNextActivity()
                }
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            }
        )
    }

    private fun startNextActivity() {

    }

}