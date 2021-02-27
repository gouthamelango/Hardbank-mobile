package com.example.hardbank;

public class SampleProject {

    String image;
    String projectid;
    String title;

    public  SampleProject(){

    }
    public  SampleProject(String image, String projectid, String title){
        this.image =  image;
        this.projectid = projectid;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getProjectid() {
        return projectid;
    }

    public String getTitle() {
        return title;
    }
}
