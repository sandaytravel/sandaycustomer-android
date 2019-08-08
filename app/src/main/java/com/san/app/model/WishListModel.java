package com.san.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class WishListModel {

    @SerializedName("payload")
    @Expose
    private List<Payload> payload = null;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    public List<Payload> getPayload() {
        return payload;
    }

    public void setPayload(List<Payload> payload) {
        this.payload = payload;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Payload {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("activitymain_id")
        @Expose
        private Integer activitymainId;
        @SerializedName("activity_whishlistId")
        @Expose
        private Integer activityWhishlistId;
        @SerializedName("total_booked")
        @Expose
        private Integer totalBooked;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("subtitle")
        @Expose
        private String subtitle;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("actual_price")
        @Expose
        private String actualPrice;
        @SerializedName("display_price")
        @Expose
        private String displayPrice;
        @SerializedName("wishlilst")
        @Expose
        private String wishlilst;
        @SerializedName("total_review")
        @Expose
        private Integer totalReview;
        @SerializedName("average_review")
        @Expose
        private Double averageReview;

        public Integer getTotalBooked() {
            return totalBooked;
        }

        public void setTotalBooked(Integer totalBooked) {
            this.totalBooked = totalBooked;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getActivitymainId() {
            return activitymainId;
        }

        public void setActivitymainId(Integer activitymainId) {
            this.activitymainId = activitymainId;
        }

        public Integer getActivityWhishlistId() {
            return activityWhishlistId;
        }

        public void setActivityWhishlistId(Integer activityWhishlistId) {
            this.activityWhishlistId = activityWhishlistId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getActualPrice() {
            return actualPrice;
        }

        public void setActualPrice(String actualPrice) {
            this.actualPrice = actualPrice;
        }

        public String getDisplayPrice() {
            return displayPrice;
        }

        public void setDisplayPrice(String displayPrice) {
            this.displayPrice = displayPrice;
        }

        public String getWishlilst() {
            return wishlilst;
        }

        public void setWishlilst(String wishlilst) {
            this.wishlilst = wishlilst;
        }

        public Integer getTotalReview() {
            return totalReview;
        }

        public void setTotalReview(Integer totalReview) {
            this.totalReview = totalReview;
        }

        public Double getAverageReview() {
            return averageReview;
        }

        public void setAverageReview(Double averageReview) {
            this.averageReview = averageReview;
        }
    }

}