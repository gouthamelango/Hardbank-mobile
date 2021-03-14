package com.example.hardbank;

import com.google.firebase.Timestamp;

public class OrderModel {

    String customerid;
    Timestamp date;
    String deliveryaddress;
    String  orderid;
    String productid;
    String quantity;
    String sellerid;
    String status;
    String type;

    public OrderModel() {
    }

    public OrderModel(String customerid, Timestamp date, String deliveryaddress, String orderid, String productid, String quantity, String sellerid, String status, String type) {
        this.customerid = customerid;
        this.date = date;
        this.deliveryaddress = deliveryaddress;
        this.orderid = orderid;
        this.productid = productid;
        this.quantity = quantity;
        this.sellerid = sellerid;
        this.status = status;
        this.type = type;
    }

    public String getCustomerid() {
        return customerid;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getDeliveryaddress() {
        return deliveryaddress;
    }

    public String getOrderid() {
        return orderid;
    }

    public String getProductid() {
        return productid;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSellerid() {
        return sellerid;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
}
