package com.app.car_spares.models;

public class PartModelClass {
    private String id;
    private String picUrl;
    private String name;
    private String description;
    private float price;
    private String userId;

    public PartModelClass() {}

    public PartModelClass(String id, String picUrl, String name, String description, float price, String userId) {
        this.id = id;
        this.picUrl = picUrl;
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public String getUserId() {
        return userId;
    }
}
