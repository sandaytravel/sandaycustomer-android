package com.san.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActivityListModel {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("payload")
    @Expose
    private List<Payload> payload = null;

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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Payload> getPayload() {
        return payload;
    }

    public void setPayload(List<Payload> payload) {
        this.payload = payload;
    }

    public class Payload {

        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
        @SerializedName("city_id")
        @Expose
        private Integer cityId;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("subtitle")
        @Expose
        private String subtitle;
        @SerializedName("fullsize_image")
        @Expose
        private String fullsizeImage;
        @SerializedName("resized_image")
        @Expose
        private String resizedImage;
        @SerializedName("display_price")
        @Expose
        private String displayPrice;
        @SerializedName("actual_price")
        @Expose
        private String actualPrice;
        @SerializedName("total_review")
        @Expose
        private Integer totalReview;
        @SerializedName("average_review")
        @Expose
        private Double averageReview;
        @SerializedName("total_booked")
        @Expose
        private Integer totalBooked;

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
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

        public String getFullsizeImage() {
            return fullsizeImage;
        }

        public void setFullsizeImage(String fullsizeImage) {
            this.fullsizeImage = fullsizeImage;
        }

        public String getResizedImage() {
            return resizedImage;
        }

        public void setResizedImage(String resizedImage) {
            this.resizedImage = resizedImage;
        }

        public String getDisplayPrice() {
            return displayPrice;
        }

        public void setDisplayPrice(String displayPrice) {
            this.displayPrice = displayPrice;
        }

        public String getActualPrice() {
            return actualPrice;
        }

        public void setActualPrice(String actualPrice) {
            this.actualPrice = actualPrice;
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

        public Integer getTotalBooked() {
            return totalBooked;
        }

        public void setTotalBooked(Integer totalBooked) {
            this.totalBooked = totalBooked;
        }
    }

}
