package com.deemons.supermarketassistant.net.model;

import com.deemons.supermarketassistant.sql.bmob.GoodsModel;

/**
 * author： deemons
 * date:    2019/1/29
 * desc:
 */
public class GoodsBean2 {

    /**
     * code : 1
     * msg : 数据返回成功
     * data : {"goodsName":"脉动维生素饮料（水蜜桃口味）600ml","barcode":"6902538005141","price":"3.80","img":"http://www.anccnet.com/userfile/uploada/gra/1608173616/06902538005141/m06902538005141.1.jpg","brand":"达能","supplier":"达能(中国)食品饮料有限公司","standard":"600ml"}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * goodsName : 脉动维生素饮料（水蜜桃口味）600ml
         * barcode : 6902538005141
         * price : 3.80
         * img : http://www.anccnet.com/userfile/uploada/gra/1608173616/06902538005141/m06902538005141.1.jpg
         * brand : 达能
         * supplier : 达能(中国)食品饮料有限公司
         * standard : 600ml
         */

        public String goodsName;
        public String barcode;
        public String price;
        public String img;
        public String brand;
        public String supplier;
        public String standard;

        public GoodsModel convert() {
            GoodsModel model = new GoodsModel();
            model.barCode = barcode;
            model.name = goodsName;
            model.image = img;
            model.price = price;
            model.standard = standard;
            model.brand = brand;
            model.supplier = supplier;

            return model;
        }
    }
}
