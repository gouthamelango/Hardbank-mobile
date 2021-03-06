package com.example.hardbank;

public class Product {
    private  String productname;
    private  int productprice;
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

    public Product(String productname, int productprice,String category, String id, String image, String productbrand,
                   String productdeliveryprice, String productdescription, String verified, String reason){
        this.productname =  productname;
        this.productprice = productprice;
        this.productdeliveryprice = productdeliveryprice;
        this.category = category;
        this.id = id;
        this.image = image;
        this.productbrand = productbrand;
        this.productdescription = productdescription;
        this.reason = reason;
        this.verified = verified;

    }

    public String getProductname() {
        return productname;
    }

    public String getProductprice() {
        return String.valueOf(productprice);
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
