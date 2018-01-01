package com.example.root.firebasetest.data.model;

/**
 * Created by root on 29/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;


public class SendData{

    @SerializedName("product_title")
    @Expose
    private String productTitle;
    @SerializedName("product_discount")
    @Expose
    private String productDiscount;
    @SerializedName("product_store")
    @Expose
    private String productStore;

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductStore() {
        return productStore;
    }

    public void setProductStore(String productStore) {
        this.productStore = productStore;
    }



}
