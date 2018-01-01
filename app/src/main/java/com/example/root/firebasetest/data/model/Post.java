package com.example.root.firebasetest.data.model;

/**
 * Created by root on 29/12/17.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Post {

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("image_result")
    @Expose
    private String imageResult;
    @SerializedName("product_discount")
    @Expose
    private String productDiscount;
    @SerializedName("product_store")
    @Expose
    private String productStore;
    @SerializedName("")
    @Expose
    private String productTitle;
    @SerializedName("resource_uri")
    @Expose
    private String resourceUri;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageResult() {
        return imageResult;
    }

    public void setImageResult(String imageResult) {
        this.imageResult = imageResult;
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

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    @Override
    public String toString() {
        return "Post{" +
                "createdAt='" + createdAt + '\'' +
                ", id=" + id +
                ", imageResult='" + imageResult + '\'' +
                ", productDiscount='" + productDiscount + '\'' +
                ", productStore='" + productStore + '\'' +
                ", productTitle='" + productTitle + '\'' +
                ", resourceUri='" + resourceUri + '\'' +
                '}';
    }

}
