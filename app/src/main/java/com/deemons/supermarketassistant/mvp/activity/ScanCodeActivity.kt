package com.deemons.supermarketassistant.mvp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.SurfaceHolder
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.deemons.supermarketassistant.R
import com.deemons.supermarketassistant.base.BaseActivity
import com.deemons.supermarketassistant.databinding.ActivityScanCodeBinding
import com.deemons.supermarketassistant.di.component.ActivityComponent
import com.deemons.supermarketassistant.mvp.presenter.ScanCodePresenter
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.vondear.rxfeature.module.scaner.CameraManager
import com.vondear.rxfeature.module.scaner.decoding.InactivityTimer
import com.vondear.rxtool.RxAnimationTool
import com.vondear.rxtool.RxBarTool
import com.vondear.rxtool.RxBeepTool
import io.reactivex.Observable
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * author： deemons
 * date:    2019/1/28
 * desc:
 */
class ScanCodeActivity : BaseActivity<ScanCodePresenter, ActivityScanCodeBinding>(), ScanCodePresenter.View {


    /**
     * 是否有预览
     */
    private var hasSurface: Boolean = false

    /**
     * 闪光灯开启状态
     */
    private var mFlashing = true


    private val inactivityTimer: InactivityTimer by lazy { InactivityTimer(this) }


    private val decodeThread: DecodeThread by lazy { DecodeThread() }

    private val adapter by lazy { ScanCodeAdapter(mutableListOf()) }

    override fun getLayout(): Int = R.layout.activity_scan_code

    override fun componentInject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun initEventAndData() {
        RxBarTool.setTransparentStatusBar(this)

        initPermission()

        initRv()

        //初始化 CameraManager
        CameraManager.init(this)
        //只是为了初始化
        inactivityTimer.toString()

        RxAnimationTool.ScaleUpDowm(mBinding.captureScanLine)


        initListener()

    }

    private fun initRv() {

        mBinding.scanRv.adapter = adapter
        mBinding.scanRv.layoutManager = LinearLayoutManager(this)

        adapter.openLoadAnimation()


        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(mBinding.scanRv)


        // 开启滑动删除
        adapter.enableSwipeItem()
        adapter.setOnItemSwipeListener(object : OnItemSwipeListener {
            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                mPresenter.deleteCode(adapter.data.get(pos).id)
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
    }

    private fun initListener() {
        mBinding.scanBack.setOnClickListener { onBackPressed() }

        mBinding.scanLight.setOnClickListener {
            if (mFlashing) { // 开闪光灯
                CameraManager.get().openLight()
            } else { // 关闪光灯
                CameraManager.get().offLight()
            }
            mFlashing = !mFlashing
        }
    }


    override fun onResume() {
        super.onResume()
        val surfaceHolder = mBinding.capturePreview.holder
        if (hasSurface) {
            //Camera初始化
            initCamera(surfaceHolder)
        } else {
            surfaceHolder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                override fun surfaceCreated(holder: SurfaceHolder) {
                    if (!hasSurface) {
                        hasSurface = true
                        initCamera(holder)
                    }
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    hasSurface = false
                }
            })
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    override fun onPause() {
        super.onPause()

        decodeThread.interrupt()
        CameraManager.get().stopPreview()
        CameraManager.get().closeDriver()
    }

    override fun onDestroy() {
        inactivityTimer.shutdown()
        super.onDestroy()
    }


    private fun initPermission() {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }


    fun setCropWidth(cropWidth: Int) {
        CameraManager.FRAME_WIDTH = cropWidth

    }


    fun setCropHeight(cropHeight: Int) {
        CameraManager.FRAME_HEIGHT = cropHeight
        CameraManager.FRAME_MARGINTOP
    }


    private fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder)
            val point = CameraManager.get().cameraResolution
            val width = AtomicInteger(point.y)
            val height = AtomicInteger(point.x)
            val cropWidth = mBinding.captureCropLayout.width * width.get() / mBinding.root.width
            val cropHeight = mBinding.captureCropLayout.height * height.get() / mBinding.root.height
            setCropWidth(cropWidth)
            setCropHeight(cropHeight)
            CameraManager.FRAME_MARGINTOP =  (mBinding.root.height - cropHeight) / 2 - mBinding.captureCropLayout.top
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            return
        } catch (ioe: RuntimeException) {
            ioe.printStackTrace()
            return
        }

        decodeThread.start()
        CameraManager.get().startPreview()
        CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode)
        CameraManager.get().requestAutoFocus(decodeThread.getHandler(), R.id.auto_focus)
    }

    @SuppressLint("CheckResult")
    override fun handleDecode(result: ScanCodeViewBean) {
        inactivityTimer.onActivity()
        //扫描成功之后的振动与声音提示
        RxBeepTool.playBeep(this, true)

        Log.v("二维码/条形码 扫描结果", result.code)

        adapter.addData(0, result)
        mBinding.scanRv.scrollToPosition(0)


        //重新开始扫描
        Observable.timer(2, TimeUnit.SECONDS)
            .subscribe({
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode)
                CameraManager.get().requestAutoFocus(decodeThread.getHandler(), R.id.auto_focus)
            }) { it.printStackTrace() }
    }


    internal class ScanCodeAdapter(data: MutableList<ScanCodeViewBean>) :
        BaseItemDraggableAdapter<ScanCodeViewBean, BaseViewHolder>(R.layout.item_rv_scan_code, data) {
        override fun convert(helper: BaseViewHolder, item: ScanCodeViewBean) {
            helper.setText(R.id.item_index, (this.itemCount - helper.layoutPosition).toString())
                .setText(R.id.item_code, item.code)
                .setText(R.id.item_time, item.time)

        }

    }


    data class ScanCodeViewBean(val id: Long, val code: String, val time: String)


    // 一堆乱七八糟的东西
    //=================================解析结果 及 后续处理 end ===========

    internal inner class DecodeThread : Thread() {

        private val handlerInitLatch: CountDownLatch
        private var handler: Handler? = null

        init {
            handlerInitLatch = CountDownLatch(1)
        }

        fun getHandler(): Handler? {
            try {
                handlerInitLatch.await()
            } catch (ie: InterruptedException) {
                // continue?
            }

            return handler
        }

        override fun run() {
            Looper.prepare()
            handler = DecodeHandler()
            handlerInitLatch.countDown()
            Looper.loop()
        }
    }

    internal inner class DecodeHandler : Handler() {


        private val multiFormatReader by lazy { getFormatReader() }


        override fun handleMessage(message: Message) {
            if (message.what === R.id.auto_focus) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus)
            } else if (message.what === R.id.decode) {
                val result = decode(message.obj as ByteArray, message.arg1, message.arg2)
                if (result == null) {
                    CameraManager.get().requestPreviewFrame(this, R.id.decode)
                } else {
                    mPresenter.decode(result.text)
                }
            } else if (message.what === R.id.quit) {
                Looper.myLooper()?.quit()
            }
        }


        private fun decode(data: ByteArray, width: Int, height: Int): Result? {
            var width = width
            var height = height
            val start = System.currentTimeMillis()
            var rawResult: Result? = null

            //modify here
            val rotatedData = ByteArray(data.size)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    rotatedData[x * height + height - y - 1] = data[x + y * width]
                }
            }
            // Here we are swapping, that's the difference to #11
            val tmp = width
            width = height
            height = tmp

            val source = CameraManager.get().buildLuminanceSource(rotatedData, width, height)
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap)
            } catch (e: ReaderException) {
                // continue
            } finally {
                multiFormatReader.reset()
            }

            return rawResult
        }


        private fun getFormatReader(): MultiFormatReader {
            val multiFormatReader = MultiFormatReader()

            // 解码的参数
            val hints = Hashtable<DecodeHintType, Any>(2)
            // 可以解析的编码类型
            var decodeFormats: Vector<BarcodeFormat>? = Vector<BarcodeFormat>()
            if (decodeFormats == null || decodeFormats.isEmpty()) {
                decodeFormats = Vector<BarcodeFormat>()

                val PRODUCT_FORMATS = Vector<BarcodeFormat>(5)
                PRODUCT_FORMATS.add(BarcodeFormat.UPC_A)
                PRODUCT_FORMATS.add(BarcodeFormat.UPC_E)
                PRODUCT_FORMATS.add(BarcodeFormat.EAN_13)
                PRODUCT_FORMATS.add(BarcodeFormat.EAN_8)
                // PRODUCT_FORMATS.add(BarcodeFormat.RSS14);
                val ONE_D_FORMATS = Vector<BarcodeFormat>(PRODUCT_FORMATS.size + 4)
                ONE_D_FORMATS.addAll(PRODUCT_FORMATS)
                ONE_D_FORMATS.add(BarcodeFormat.CODE_39)
                ONE_D_FORMATS.add(BarcodeFormat.CODE_93)
                ONE_D_FORMATS.add(BarcodeFormat.CODE_128)
                ONE_D_FORMATS.add(BarcodeFormat.ITF)
                val QR_CODE_FORMATS = Vector<BarcodeFormat>(1)
                QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE)
                val DATA_MATRIX_FORMATS = Vector<BarcodeFormat>(1)
                DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX)

                // 这里设置可扫描的类型，我这里选择了都支持
                decodeFormats.addAll(ONE_D_FORMATS)
                decodeFormats.addAll(QR_CODE_FORMATS)
                decodeFormats.addAll(DATA_MATRIX_FORMATS)
            }
            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats)

            multiFormatReader.setHints(hints)
            return multiFormatReader
        }


    }

}