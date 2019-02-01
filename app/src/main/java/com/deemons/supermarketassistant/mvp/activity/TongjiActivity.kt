package com.deemons.supermarketassistant.mvp.activity

import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.CountListener
import com.blankj.utilcode.util.KeyboardUtils
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.base.EPresenter
import com.deemons.supermarketassistant.databinding.ActivityTongjiBinding
import com.deemons.supermarketassistant.databinding.DialogEditBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.expand.bindLoadingDialog
import com.deemons.supermarketassistant.sql.bmob.Tmp_Goods
import com.deemons.supermarketassistant.tools.ResUtils
import com.deemons.supermarketassistant.tools.bind
import com.deemons.supermarketassistant.tools.io_main
import com.vondear.rxtool.view.RxToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers


class TongjiActivity : BaseActivity<EPresenter, ActivityTongjiBinding>() {

    var page = 0


    private val adapter by lazy { PandianActivity.SearchAdapter(mutableListOf()) }

    override fun getLayout(): Int = R.layout.activity_tongji

    override fun componentInject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initEventAndData() {

        mBinding.productBack.setOnClickListener { onBackPressed() }


        mBinding.productRv.layoutManager = LinearLayoutManager(this)
        mBinding.productRv.adapter = adapter

        adapter.loadMoreEnd(true)
        adapter.setOnLoadMoreListener({ findData() }, mBinding.productRv)

        adapter.setOnItemLongClickListener { adapter, view, position ->
            val goods = this.adapter.data.getOrNull(position) ?: return@setOnItemLongClickListener false
            showEditDialog(goods)
            true
        }

        adapter.setOnItemClickListener{adapter, view, position ->
            val goods = this.adapter.data.getOrNull(position) ?: return@setOnItemClickListener
            ProductDetailActivity.startActivity(goods.barCode )
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
            .doOnNext { it.amount = it.count * it.realPrice.toFloat() }
            .doOnNext { it.updateSync() }
            .io_main()
            .bindLoadingDialog(this)
            .subscribe({
                RxToast.showToast("修改成功")
                page = 0
                findData()
            }, { it.printStackTrace() })
            .bind(mPresenter.mDisposable)
    }

    private fun findData() {

        Observable.just(BmobQuery<Tmp_Goods>())
            .bindLoadingDialog(this)
            .flatMap {
                if (page <= 0) {
                    it.addWhereGreaterThanOrEqualTo("count", 1)
                        .countObservable(Tmp_Goods::class.java)
                        .doOnNext { adapter.count = it }
                        .map { BmobQuery<Tmp_Goods>() }
                } else {
                    Observable.just(it)
                }
            }
            .flatMap {
                it.setLimit(500).setSkip(500 * (page++))
                    .addWhereGreaterThanOrEqualTo("count", 1)
                    .findObjectsObservable(Tmp_Goods::class.java)
            }
            .doOnNext {
                it.forEach {
                    it.realPrice = it.realPrice.replace("￥", "").replace(",", "")
                    it.sellingPrice = it.sellingPrice.replace("￥", "").replace(",", "")
                    it.amount = it.realPrice.toFloat() * it.count
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { amount() }
            .subscribe({
                if (page <= 1) {
                    adapter.setNewData(it)
                } else if (it.size > 0) {
                    adapter.addData(it)
                    adapter.loadMoreComplete()
                } else {
                    adapter.loadMoreEnd(true)
                }
            }, {
                it.printStackTrace()
                adapter.setNewData(mutableListOf())
            }).bind(mPresenter.mDisposable)


    }

    private fun amount() {
        BmobQuery<Tmp_Goods>().sum(arrayOf("amount")).findStatisticsObservable(Tmp_Goods::class.java)
            .map { it.getJSONObject(0).getDouble("_sumAmount") }
            .subscribe({
                mBinding.productCount.text = ResUtils.getString("￥%.2f", it)
            }, { it.printStackTrace() })
            .bind(mPresenter.mDisposable)
    }

}
