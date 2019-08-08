package com.san.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SearchCityArractionListModel {

    @SerializedName("payload")
    @Expose
    private Payload payload;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
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

    public class Activity {

        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
        @SerializedName("activity_title")
        @Expose
        private String activityTitle;
        @SerializedName("city_name")
        @Expose
        private String cityName;

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public String getActivityTitle() {
            return activityTitle;
        }

        public void setActivityTitle(String activityTitle) {
            this.activityTitle = activityTitle;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

    }

    public class City {

        @SerializedName("city_id")
        @Expose
        private Integer cityId;
        @SerializedName("city_name")
        @Expose
        private String cityName;

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

    }

    public class Payload {

        @SerializedName("city")
        @Expose
        private List<City> city = null;
        @SerializedName("activity")
        @Expose
        private List<Activity> activity = null;

        public List<City> getCity() {
            return city;
        }

        public void setCity(List<City> city) {
            this.city = city;
        }

        public List<Activity> getActivity() {
            return activity;
        }

        public void setActivity(List<Activity> activity) {
            this.activity = activity;
        }

    }

}