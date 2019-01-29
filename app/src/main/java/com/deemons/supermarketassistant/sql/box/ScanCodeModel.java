package com.deemons.supermarketassistant.sql.box;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * author： deemons
 * date:    2019/1/28
 * desc:
 */
@Entity
public class ScanCodeModel {

    @Id
    public long id;

    public String code;

    public long createTime;

}
