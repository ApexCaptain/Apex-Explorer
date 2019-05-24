package com.gmail.ayteneve93.apex.explorer.activities.entry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gmail.ayteneve93.apex.explorer.activities.intro.IntroActivity

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Entry Activity에서 런칭 후 일반 Installation 런칭의 경우 Intro로 넘어가고 아니면
         * 다른 Dynamic 모듈을 바로 호출. URI 프리픽스 제한이 1달 후에 해제됨. 6/24일에 다시 봐야할듯
         */
        // 일단 바로 Intro 로 변경
        startActivity(Intent(this, IntroActivity::class.java))
    }
}


