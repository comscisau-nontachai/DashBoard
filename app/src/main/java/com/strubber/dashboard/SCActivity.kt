package com.strubber.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SCActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sc)

        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()

    }
}
