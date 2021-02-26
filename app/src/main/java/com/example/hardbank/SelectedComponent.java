package com.example.hardbank;

public class SelectedComponent {

    String image;
    String productid;
    String productname;

    public  SelectedComponent(String image, String productid, String productname){
        this.image = image;
        this.productid = productid;
        this.productname =  productname;
    }
    public  SelectedComponent(){

    }

    public String getImage() {
        return image;
    }

    public String getProductid() {
        return productid;
    }

    public String getProductname() {
        return productname;
    }
}
