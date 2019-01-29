package com.deemons.supermarketassistant.tools

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

/**
 * author： deemons
 * date:   2019/1/27
 * desc:   rx 相关帮助类
 */

val schedulerIO by lazy { Schedulers.from(Executors.newFixedThreadPool(10)) }


fun <T> Observable<T>.io_main(): Observable<T> {
    return io_()._main()
}


fun <T> Observable<T>.io_(): Observable<T> {
    return this.subscribeOn(schedulerIO)
}


fun <T> Observable<T>.main_(): Observable<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread())
}


fun <T> Observable<T>._io(): Observable<T> {
    return this.observeOn(schedulerIO)
}


fun <T> Observable<T>._main(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}


fun <T> Observable<T>.subscribeSafe(function: (T) -> Unit): Disposable {
    return this.subscribe(function, { it.printStackTrace() })
}


fun Disposable.bind(b: CompositeDisposable) {
    b.add(this)
}

