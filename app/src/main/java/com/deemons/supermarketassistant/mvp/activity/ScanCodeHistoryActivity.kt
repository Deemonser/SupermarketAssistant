package com.deemons.supermarketassistant.mvp.activity

import android.graphics.Canvas
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.base.EPresenter
import com.deemons.supermarketassistant.databinding.ActivityScanCodeHistoryBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.sql.model.ScanCodeModel
import com.deemons.supermarketassistant.tools.TimeUtils
import com.vondear.rxtool.RxClipboardTool
import com.vondear.rxtool.view.RxToast
import io.objectbox.BoxStore
import org.joda.time.DateTime
import javax.inject.Inject

class ScanCodeHistoryActivity : BaseActivity<EPresenter, ActivityScanCodeHistoryBinding>() {

    private val adapter by lazy { ScanCodeActivity.ScanCodeAdapter(mutableListOf()) }

    @Inject
    lateinit var boxStore: BoxStore


    val scanCodeModelStore by lazy { boxStore.boxFor(ScanCodeModel::class.java) }

    override fun getLayout(): Int = R.layout.activity_scan_code_history

    override fun componentInject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initEventAndData() {

        mBinding.historyBack.setOnClickListener { onBackPressed() }

        initRv()

        initDate()
    }

    private fun initRv() {


        adapter.openLoadAnimation()


        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(mBinding.historyRv)


        // 开启滑动删除
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(object : OnItemSwipeListener {
            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                scanCodeModelStore.remove(adapter.data.get(pos).id)
            }

            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
            }

            override fun onItemSwipeMoving(
                canvas: Canvas?,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                isCurrentlyActive: Boolean
            ) {

            }

            override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

            }

        })


        mBinding.historyRv.layoutManager = LinearLayoutManager(this)
        mBinding.historyRv.adapter = adapter

    }

    private fun initDate() {

        val list = scanCodeModelStore.all.map {
            ScanCodeActivity.ScanCodeViewBean(
                it.id,
                it.code,
                DateTime(it.createTime).toString(TimeUtils.TIME_NORMAL)
            )
        }.toList()

        adapter.setNewData(list)
    }


    fun copy(view: View) {
        val builder = StringBuilder()
        scanCodeModelStore.all.forEach {
            builder.append(it.code)
            builder.append("\n")
        }

        RxClipboardTool.copyText(this, builder.toString())
        RxToast.showToast("复制成功")
    }

    fun delete(view: View) {
        scanCodeModelStore.removeAll()
        adapter.setNewData(listOf())
    }

}
