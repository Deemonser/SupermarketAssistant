package com.deemons.supermarketassistant.tools;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import com.deemons.supermarketassistant.BuildConfig;
import com.vondear.rxtool.RxTool;

/**
 * 创建者      deemons
 * 创建时间    2018/10/23
 * 描述       初始化的工具类
 */

public class InitUtils {

    private static Boolean isDebug = null;

    public static void init(@NonNull Application application) {
        syncIsDebug(application);
        com.blankj.utilcode.util.Utils.init(application);
        LogUtils.initLogger();
        TimeUtils.init(application);
        RxTool.init(application);
    }


    /**
     * 是否是 Debug 模式
     * 因在Library 模块中使用{@link BuildConfig#DEBUG}时，都会返回 false，因此建议使用此方式判断。
     */
    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * Sync lib debug with app's debug value. Should be called in module Application
     *
     * @param context Context
     */
    private static void syncIsDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null
                    && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }
}
