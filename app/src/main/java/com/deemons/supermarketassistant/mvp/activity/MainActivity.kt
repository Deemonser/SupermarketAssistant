package com.deemons.supermarketassistant.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.deemons.supermarketassistant.R
import com.vondear.rxfeature.activity.ActivityScanerCode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, ActivityScanerCode::class.java))

    }
}
