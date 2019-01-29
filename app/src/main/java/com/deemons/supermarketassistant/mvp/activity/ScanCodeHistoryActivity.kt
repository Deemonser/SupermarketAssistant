package com.deemons.supermarketassistant.mvp.activity

import android.graphics.Canvas
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import cn.bmob.v3.BmobQuery
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.base.EPresenter
import com.deemons.supermarketassistant.databinding.ActivityScanCodeHistoryBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.expand.bindLoadingDialog
import com.deemons.supermarketassistant.sql.bmob.ScanCodeModel
import com.deemons.supermarketassistant.tools.TimeUtils
import com.deemons.supermarketassistant.tools.bind
import com.deemons.supermarketassistant.tools.subscribeSafe
import com.vondear.rxtool.RxClipboardTool
import com.vondear.rxtool.view.RxToast
import io.reactivex.Observable
import org.joda.time.DateTime

class ScanCodeHistoryActivity : BaseActivity<EPresenter, ActivityScanCodeHistoryBinding>() {

    private val adapter by lazy { ScanCodeActivity.ScanCodeAdapter(mutableListOf()) }

    val bmobQuery by lazy { BmobQuery<ScanCodeModel>() }

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
                bmobQuery.getObjectObservable(ScanCodeModel::class.java, adapter.data.get(pos).id)
                    .flatMap { it.deleteObservable(it.objectId) }
                    .subscribeSafe { }
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

        bmobQuery.findObjectsObservable(ScanCodeModel::class.java)
            .flatMap { Observable.fromIterable(it) }
            .map {
                ScanCodeActivity.ScanCodeViewBean(
                    it.objectId,
                    it.code,
                    "", "", "",
                    DateTime(it.createTime).toString(TimeUtils.TIME_NORMAL)
                )
            }
            .toList()
            .subscribe({
                adapter.setNewData(it)
            }, { it.printStackTrace() })
            .bind(mPresenter.mDisposable)
    }


    fun copy(view: View) {
        bmobQuery.findObjectsObservable(ScanCodeModel::class.java)
            .flatMap { Observable.fromIterable(it) }
            .map { it.code }
            .reduce { t1, t2 -> t1 + "\n" + t2 }
            .subscribe({
                RxClipboardTool.copyText(getContext(), it)
                RxToast.showToast("复制成功")
            }, { it.printStackTrace() })
            .bind(mPresenter.mDisposable)
    }

    fun delete(view: View) {

        bmobQuery.findObjectsObservable(ScanCodeModel::class.java)
            .bindLoadingDialog(this)
            .flatMap { Observable.fromIterable(it) }
            .flatMap { it.deleteObservable(it.objectId) }
            .toList()
            .subscribe({ adapter.setNewData(listOf()) }, { it.printStackTrace() })
            .bind(mPresenter.mDisposable)
    }

}
