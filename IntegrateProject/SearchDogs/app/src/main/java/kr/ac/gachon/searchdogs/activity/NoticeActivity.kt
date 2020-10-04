package kr.ac.gachon.searchdogs.activity

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.widget.Button
import android.widget.TextView

class NoticeActivity : Activity() {
    private var notice : TextView? = null
    private var okbtn : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(kr.ac.gachon.searchdogs.R.layout.activity_notice)

        notice = findViewById(kr.ac.gachon.searchdogs.R.id.notice_txt)
        okbtn = findViewById(kr.ac.gachon.searchdogs.R.id.closebtn)

        notice?.text = "테스트용 공지사항"

        findViewById<Button>(kr.ac.gachon.searchdogs.R.id.closebtn).setOnClickListener {
            this.finish()
        }
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        if (event.action == MotionEvent.ACTION_OUTSIDE){
            return false
        }
        return true
    }

    override fun onBackPressed() {
        return
    }
}
