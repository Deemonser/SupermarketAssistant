package com.deemons.supermarketassistant.sql.bmob;

import cn.bmob.v3.BmobObject;

/**
 * author： deemons
 * date:    2019/1/29
 * desc:
 */
public class GoodsModel extends BmobObject {

    /**
     * 条形码
     */
    public String barCode;

    /**
     * 名称
     */
    public String name;

    /**
     * 图片
     */
    public String image;

    /**
     * 参考价格
     */
    public String price;

    /**
     * 规格
     */
    public String standard;

    /**
     * 供应商
     */
    public String supplier;

    /**
     * 品牌
     */
    public String brand;


    /**
     * 成本价
     */
    public float realPrice;

    /**
     * 售价
     */
    public float sellingPrice;

    /**
     * 数量
     */
    public int count;

}
