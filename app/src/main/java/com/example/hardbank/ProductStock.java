package com.example.hardbank;

public class ProductStock  {

    String category;
    String productname;
    String stock;
    String productid;

    public ProductStock() {
    }
    public ProductStock(String category, String productname, String stock,String productid) {
        this.category = category;
        this.productname = productname;
        this.stock = stock;
        this.productid = productid;
    }

    public String getProductid() {
        return productid;
    }

    public String getCategory() {
        return category;
    }

    public String getProductname() {
        return productname;
    }

    public String getStock() {
        return stock;
    }
}
