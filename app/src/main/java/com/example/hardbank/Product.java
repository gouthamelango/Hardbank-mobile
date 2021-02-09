package com.example.hardbank;

public class Product {
    private  String productname;
    private  String productprice;
    private  String category;
    private  String id;
    private  String image;
    private  String productbrand;
    private  String productdeliveryprice;
    private  String productdescription;
    private  String verified;

    public String getReason() {
        return reason;
    }

    private  String  reason;

    public Product(){

    }

    public Product(String productname, String productprice,String category, String id, String image, String productbrand,
                   String productdeliveryprice, String productdescription, String verified, String reason){
        this.productname =  productname;
        this.productprice = productprice;
        this.productdeliveryprice = productdeliveryprice;

    }

    public String getProductname() {
        return productname;
    }

    public String getProductprice() {
        return productprice;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getProductbrand() {
        return productbrand;
    }

    public String getProductdeliveryprice() {
        return productdeliveryprice;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public String getVerified() {
        return verified;
    }
}
