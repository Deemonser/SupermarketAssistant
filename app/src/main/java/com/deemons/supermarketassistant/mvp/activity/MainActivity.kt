package com.deemons.supermarketassistant.mvp.activity

import android.content.Intent
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.base.EPresenter
import com.deemons.supermarketassistant.databinding.ActivityMainBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.vondear.rxtool.view.RxToast

class MainActivity : BaseActivity<EPresenter, ActivityMainBinding>() {

    override fun getLayout(): Int = R.layout.activity_main

    override fun componentInject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initEventAndData() {

    }


    fun scanCode(view: View) {
//        startActivity(Intent(this, ScanCodeActivity::class.java))
//        RxToast.showToast("该功能暂时关闭！")

        val data = mutableListOf<Int>()
        for (i in 0..10) {
            data.add(i)
        }

        val build = OptionsPickerBuilder(this) { options1, options2, options3, v ->

        }
            .build<Int>()

        build.setNPicker(data,data,data)

        build.show()

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
