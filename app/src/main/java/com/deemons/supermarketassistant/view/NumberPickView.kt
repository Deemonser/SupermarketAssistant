package com.deemons.supermarketassistant.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.deemons.supermarketassistant.R

import java.util.ArrayList

/**
 * author： deemons
 * date:    2019/2/1
 * desc:
 */
class NumberPickView : ConstraintLayout {

    private val mWheelView by lazy { ArrayList<WheelView>() }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()

    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_number_pick, this, true)
        mWheelView.add(findViewById(R.id.number_pick_1))
        mWheelView.add(findViewById(R.id.number_pick_2))
        mWheelView.add(findViewById(R.id.number_pick_3))
        mWheelView.add(findViewById(R.id.number_pick_4))
        mWheelView.add(findViewById(R.id.number_pick_5))
        mWheelView.add(findViewById(R.id.number_pick_6))


        val data = ArrayList<String>()
        for (i in 0..9) {
            data.add(i.toString())
        }

        mWheelView.forEach {
            it.setCyclic(false)
            it.adapter = ArrayWheelAdapter(data)
        }


    }
}
