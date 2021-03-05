package com.example.hardbank;

public class ReviewModel {

    String id;
    Float rating;
    String review;

    public ReviewModel(String id, Float rating, String review) {
        this.id = id;
        this.rating = rating;
        this.review = review;
    }

    public ReviewModel() {
    }

    public String getId() {
        return id;
    }

    public String getRating() {
        return String.valueOf(rating);
    }

    public String getReview() {
        return review;
    }
}
