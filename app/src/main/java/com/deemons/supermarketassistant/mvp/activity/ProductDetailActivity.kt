package com.deemons.supermarketassistant.mvp.activity

import android.annotation.SuppressLint
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.constant.RouterKey
import com.deemons.supermarketassistant.databinding.ActivityProductDetailBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.mvp.presenter.ProductDetailPresenter
import com.deemons.supermarketassistant.sql.bmob.GoodsModel

@Route(path = RouterKey.PRODUCT_DETAIL)
class ProductDetailActivity : BaseActivity<ProductDetailPresenter, ActivityProductDetailBinding>(),
    ProductDetailPresenter.View {

    @JvmField
    @Autowired
    var barCode: String = ""

    companion object {
        fun startActivity(barCode: String) {
            ARouter.getInstance().build(RouterKey.PRODUCT_DETAIL)
                .withString("barCode", barCode)
                .navigation()
        }
    }


    override fun getLayout(): Int = R.layout.activity_product_detail

    override fun componentInject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initEventAndData() {

        if (barCode.isBlank()) return

        initData()

        initListener()

    }

    private fun initData() {

    }

    private fun initListener() {
        mBinding.productBack.setOnClickListener { onBackPressed() }


    }


    @SuppressLint("SetTextI18n")
    override fun setViewData(data: GoodsModel) {
        Glide.with(this).load(data.image).into(mBinding.productImg)
        mBinding.productName.text = data.name
        mBinding.productPrice.text = "价格：${data.price}"
        mBinding.productStandard.text = "规格：${data.standard}"
        mBinding.productBrand.text = "品牌：${data.brand}"
        mBinding.productBarCode.text = "条码：${data.barCode}"
        mBinding.productSupplier.text = "供应商：${data.supplier}"
//        mBinding.productRealCountPrice.text = "成本总计：${data.realPrice}"
//        mBinding.productBarCode.text = "售价总计：${data.standard}"
    }

}
