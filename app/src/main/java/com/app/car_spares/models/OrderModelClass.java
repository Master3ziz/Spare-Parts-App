package com.app.car_spares.models;

public class OrderModelClass {
    private String id;
    private String partId;
    private String userId;
    private String userName;
    private String sellerId;
    private String partName;
    private float partPrice;
    private String partPic;
    private String quantity;
    private String status;

    public OrderModelClass() { }

    public OrderModelClass(String id, String partId, String userId, String userName, String sellerId, String partName,
                           float partPrice, String partPic, String quantity, String status) {
        this.id = id;
        this.partId = partId;
        this.userId = userId;
        this.userName = userName;
        this.sellerId = sellerId;
        this.partName = partName;
        this.partPrice = partPrice;
        this.partPic = partPic;
        this.quantity = quantity;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getPartId() {
        return partId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getPartName() {
        return partName;
    }

    public float getPartPrice() {
        return partPrice;
    }

    public String getPartPic() {
        return partPic;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }
}
