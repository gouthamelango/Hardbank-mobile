package com.example.hardbank;

public class ProjectContent {

    String contentid;
    String content;
    String header;
    String projectid;

    public ProjectContent(String contentid, String content, String header,String projectid){
        this.contentid = contentid;
        this.content = content;
        this.header = header;
        this.projectid = projectid;
    }

    public  ProjectContent(){

    }
    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public String getContentid() {
        return contentid;
    }

    public String getContent() {
        return content;
    }
}