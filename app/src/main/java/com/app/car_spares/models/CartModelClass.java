package com.app.car_spares.models;

public class CartModelClass {
    private String partId;
    private String picUrl;
    private String name;
    private float price;
    private String sellerId;
    private String quantity;

    public CartModelClass() { }

    public CartModelClass(String partId, String picUrl, String name, float price, String sellerId, String quantity) {
        this.partId = partId;
        this.picUrl = picUrl;
        this.name = name;
        this.price = price;
        this.sellerId = sellerId;
        this.quantity = quantity;
    }

    public String getPartId() {
        return partId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getQuantity() {
        return quantity;
    }
}
