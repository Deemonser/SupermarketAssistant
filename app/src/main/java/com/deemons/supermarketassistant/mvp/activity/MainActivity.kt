package com.deemons.supermarketassistant.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.deemons.supermarketassistant.R
import com.vondear.rxtool.view.RxToast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    fun scanCode(view: View) {
//        startActivity(Intent(this, ScanCodeActivity::class.java))
        RxToast.showToast("该功能暂时关闭！")
    }

    fun history(view: View) {
//        startActivity(Intent(this, ScanCodeHistoryActivity::class.java))
        RxToast.showToast("该功能暂时关闭！")
    }


    fun pandian(view: View) {
        startActivity(Intent(this, PandianActivity::class.java))
    }

    fun tongji(view: View) {
//        RxToast.showToast("该功能未完待续！")
        startActivity(Intent(this, TongjiActivity::class.java))
    }
}
