package com.deemons.supermarketassistant.tools;

import com.deemons.supermarketassistant.BuildConfig;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

public class LogUtils {


    public static void initLogger() {
        Logger.init("Deemons")
                .methodCount(1)
                .hideThreadInfo()
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE)
                .methodOffset(1);
    }

    public static void e(String msg, Object... args) {
        Logger.e(msg, args);
    }

    public static void e(Throwable throwable, String msg, Object... args) {
        Logger.e(throwable, msg, args);
    }

    public static void e(Throwable throwable, Object... args) {
        Logger.e(throwable, null, args);
    }

    public static void w(String msg, Object... args) {
        Logger.w(msg, args);
    }

    public static void d(String msg, Object... args) {
        Logger.d(msg, args);
    }

    public static void v(String msg, Object... args) {
        Logger.v(msg, args);
    }

    public static void i(String msg, Object... args) {
        Logger.i(msg, args);
    }

    public static void json(String json) {
        Logger.json(json);
    }

    public static void xml(String xml) {
        Logger.xml(xml);
    }


} 