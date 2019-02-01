package com.deemons.supermarketassistant.mvp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import cn.bmob.v3.BmobQuery
import com.blankj.utilcode.util.KeyboardUtils
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.base.EPresenter
import com.deemons.supermarketassistant.databinding.ActivityPandianBinding
import com.deemons.supermarketassistant.databinding.DialogEditBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.expand.bindLoadingDialog
import com.deemons.supermarketassistant.sql.bmob.Tmp_Goods
import com.deemons.supermarketassistant.tools.LogUtils
import com.deemons.supermarketassistant.tools.ResUtils
import com.deemons.supermarketassistant.tools.bind
import com.deemons.supermarketassistant.tools.io_main
import com.vondear.rxtool.view.RxToast
import io.reactivex.Observable

class PandianActivity : BaseActivity<EPresenter, ActivityPandianBinding>() {


    private val adapter by lazy { SearchAdapter(mutableListOf()) }

    override fun getLayout(): Int = R.layout.activity_pandian

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


        mBinding.productSearch.setOnClickListener {
            if (mBinding.productEdit.text.toString().isNotBlank()) {
                KeyboardUtils.hideSoftInput(this)
                findData(mBinding.productEdit.text.toString())
            }
        }

        mBinding.productScan.setOnClickListener {
            startActivityForResult(
                Intent(this, ScanCodeActivity::class.java),
                1
            )
        }

        mBinding.productEdit.setOnEditorActionListener { p0, p1, p2 ->
            LogUtils.d("setOnEditorActionListener $p1")
            if (p1 == EditorInfo.IME_ACTION_SEARCH && p0.text.isNotBlank()) {
                KeyboardUtils.hideSoftInput(this)
                findData(mBinding.productEdit.text.toString())
                return@setOnEditorActionListener false
            }
            return@setOnEditorActionListener true
        }

        mBinding.productClean.setOnClickListener {
            mBinding.productEdit.text.clear()
        }

        mBinding.productEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                // 清除 按钮显示隐藏
                if (p0.isNotEmpty()) {
                    if (View.VISIBLE != mBinding.productClean.visibility) {
                        mBinding.productClean.visibility = View.VISIBLE
                    }
                } else if (View.GONE != mBinding.productClean.visibility) {
                    mBinding.productClean.visibility = View.INVISIBLE
                }

            }

            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })


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
                mBinding.productSearch.callOnClick()
            }, { it.printStackTrace() })
            .bind(mPresenter.mDisposable)
    }

    private fun findData(code: String) {
        Observable.just(code)
            .bindLoadingDialog(this)
            .flatMap {
                BmobQuery<Tmp_Goods>().or(
                    listOf(
                        BmobQuery<Tmp_Goods>().addWhereContains("name", code),
                        BmobQuery<Tmp_Goods>().addWhereContains("barCode", code)
                    )
                )
                    .findObjectsObservable(Tmp_Goods::class.java)
            }
            .map {
                for (goods in it) {
                    goods.realPrice = goods.realPrice.replace("￥", "").replace(",", "")
                    goods.sellingPrice = goods.sellingPrice.replace("￥", "").replace(",", "")
                }
                it
            }
            .subscribe({
                adapter.count = it.size
                adapter.setNewData(it)
            }, {
                it.printStackTrace()
                adapter.setNewData(mutableListOf())
            }).bind(mPresenter.mDisposable)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            mBinding.productEdit.setText(data.getStringExtra("barCode") ?: "")
            mBinding.productEdit.setSelection(mBinding.productEdit.text.length)
            mBinding.productSearch.callOnClick()
        }
    }

    internal class SearchAdapter(data: MutableList<Tmp_Goods>) :
        BaseItemDraggableAdapter<Tmp_Goods, BaseViewHolder>(R.layout.item_rv_pandian_search, data) {

        var count = 0

        override fun convert(helper: BaseViewHolder, item: Tmp_Goods) {


            helper.setText(R.id.item_index, (count - data.indexOf(item)).toString())
                .setText(R.id.item_code, item.barCode)
                .setText(R.id.item_name, item.name)
                .setText(R.id.item_price, "进价：${item.realPrice}")
                .setText(R.id.item_selling, "售价：${item.sellingPrice}")
                .setText(R.id.item_count, "数量：${item.count}")
                .setText(R.id.item_add, ResUtils.getString("成本总计：%.2f", item.realPrice.toFloat() * item.count))

        }


    }


}
