package com.deemons.supermarketassistant.sql.bmob;

import cn.bmob.v3.BmobObject;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * author： deemons
 * date:    2019/1/28
 * desc:
 */
public class ScanCodeModel extends BmobObject {

    public String code;

    public long createTime;

}
