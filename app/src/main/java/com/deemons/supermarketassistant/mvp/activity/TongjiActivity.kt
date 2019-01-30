package com.deemons.supermarketassistant.mvp.activity

import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import cn.bmob.v3.BmobQuery
import com.blankj.utilcode.util.KeyboardUtils
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.base.EPresenter
import com.deemons.supermarketassistant.databinding.ActivityTongjiBinding
import com.deemons.supermarketassistant.databinding.DialogEditBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.expand.bindLoadingDialog
import com.deemons.supermarketassistant.sql.bmob.Tmp_Goods
import com.deemons.supermarketassistant.tools.bind
import com.deemons.supermarketassistant.tools.io_main
import com.deemons.supermarketassistant.tools.schedulerIO
import com.vondear.rxtool.view.RxToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.function.BiFunction


class TongjiActivity : BaseActivity<EPresenter, ActivityTongjiBinding>() {

    val query by lazy { BmobQuery<Tmp_Goods>() }

    private val adapter by lazy { PandianActivity.SearchAdapter(mutableListOf()) }

    override fun getLayout(): Int = R.layout.activity_tongji

    override fun componentInject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initEventAndData() {

        mBinding.productBack.setOnClickListener { onBackPressed() }


        mBinding.productRv.layoutManager = LinearLayoutManager(this)
        mBinding.productRv.adapter = adapter


        adapter.setOnItemClickListener { adapter, view, position ->
            val goods = this.adapter.data.getOrNull(position) ?: return@setOnItemClickListener
            showEditDialog(goods)
        }



        findData()

    }

    private fun showEditDialog(goods: Tmp_Goods) {
        val binding =
            DataBindingUtil.inflate<DialogEditBinding>(LayoutInflater.from(this), R.layout.dialog_edit, null, false)

        binding.realPrice.text = "进价：${goods.realPrice}"
        binding.sellingPrice.text = "售价：${goods.sellingPrice}"
        binding.count.text = "数量：${goods.count}"


        val dialog = AlertDialog.Builder(this).setView(binding.root).create()
        dialog.setCancelable(true)
        dialog.show()

        binding.commit.setOnClickListener {
            var needSave = false
            KeyboardUtils.hideSoftInput(this)
            if (binding.realPriceEdit.text.toString().isNotBlank()) {
                goods.realPrice = binding.realPriceEdit.text.toString()
                needSave = true
            }
            if (binding.sellingPriceEdit.text.toString().isNotBlank()) {
                goods.sellingPrice = binding.sellingPriceEdit.text.toString()
                needSave = true
            }
            if (binding.countEdit.text.toString().isNotBlank()) {
                goods.count = binding.countEdit.text.toString().toInt()
                needSave = true
            }
            dialog.dismiss()
            if (needSave) {
                updateGoods(goods)
            }
        }

    }

    private fun updateGoods(goods: Tmp_Goods) {
        Observable.just(goods)
            .doOnNext { it.updateSync() }
            .io_main()
            .bindLoadingDialog(this)
            .subscribe({
                RxToast.showToast("修改成功")
                findData()
            }, { it.printStackTrace() })
    }

    private fun findData() {


        Observable.just(query)
            .bindLoadingDialog(this)
            .flatMap {
                query.addWhereGreaterThanOrEqualTo("count", 1)
                    .countObservable(Tmp_Goods::class.java)
            }
            .observeOn(schedulerIO)
            .map {
                val page = it / 500
                val list = mutableListOf<Int>()
                for (i in 0..page)
                    list.add(i)
                return@map list
            }
            .map {
                val result = mutableListOf<Tmp_Goods>()
                it.forEach {
                    result.addAll(
                        query.setLimit(500).setSkip(500 * it)
                            .addWhereGreaterThanOrEqualTo("count", 1)
                            .findObjectsSync(Tmp_Goods::class.java)
                    )
                }
                result
            }
            .map {
                var countPrice = 0f
                it.forEach { countPrice += it.realPrice.replace("￥", "").replace(",", "").toFloat() * it.count }
                Pair(it, countPrice)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.setNewData(it.first)
            }, {
                it.printStackTrace()
                adapter.setNewData(mutableListOf())
            }).bind(mPresenter.mDisposable)


    }

}
