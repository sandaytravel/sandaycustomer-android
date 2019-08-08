package com.san.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewCityDetailModel{

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private Payload payload;

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

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }


    public class Category {

        @SerializedName("category_id")
        @Expose
        private Integer categoryId;
        @SerializedName("category_name")
        @Expose
        private String categoryName;
        @SerializedName("category_image")
        @Expose
        private String categoryImage;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("subcategories")
        @Expose
        private List<Subcategory> subcategories = null;

        public Integer getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryImage() {
            return categoryImage;
        }

        public void setCategoryImage(String categoryImage) {
            this.categoryImage = categoryImage;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<Subcategory> getSubcategories() {
            return subcategories;
        }

        public void setSubcategories(List<Subcategory> subcategories) {
            this.subcategories = subcategories;
        }


        public class Subcategory {

            @SerializedName("subcategory_id")
            @Expose
            private Integer subcategoryId;
            @SerializedName("subcategory_name")
            @Expose
            private String subcategoryName;
            @SerializedName("status")
            @Expose
            private String status;

            public Integer getSubcategoryId() {
                return subcategoryId;
            }

            public void setSubcategoryId(Integer subcategoryId) {
                this.subcategoryId = subcategoryId;
            }

            public String getSubcategoryName() {
                return subcategoryName;
            }

            public void setSubcategoryName(String subcategoryName) {
                this.subcategoryName = subcategoryName;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

        }

    }

    public class City {

        @SerializedName("city_id")
        @Expose
        private Integer cityId;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("image_fullsize")
        @Expose
        private String imageFullsize;
        @SerializedName("image_resized")
        @Expose
        private String imageResized;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("timezone")
        @Expose
        private String timezone;
        @SerializedName("zone_name")
        @Expose
        private String zoneName;
        @SerializedName("created_date")
        @Expose
        private String createdDate;

        public Integer getCityId() {
            return cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getImageFullsize() {
            return imageFullsize;
        }

        public void setImageFullsize(String imageFullsize) {
            this.imageFullsize = imageFullsize;
        }

        public String getImageResized() {
            return imageResized;
        }

        public void setImageResized(String imageResized) {
            this.imageResized = imageResized;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getZoneName() {
            return zoneName;
        }

        public void setZoneName(String zoneName) {
            this.zoneName = zoneName;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

    }

    public class Payload {

        @SerializedName("city")
        @Expose
        private City city;
        @SerializedName("categories")
        @Expose
        private List<Category> categories = null;
        @SerializedName("recentlyadded")
        @Expose
        private List<Recentlyadded> recentlyadded = null;
        @SerializedName("popularActivity")
        @Expose
        private List<PopularActivity> popularActivity = null;

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            this.city = city;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

        public List<Recentlyadded> getRecentlyadded() {
            return recentlyadded;
        }

        public void setRecentlyadded(List<Recentlyadded> recentlyadded) {
            this.recentlyadded = recentlyadded;
        }

        public List<PopularActivity> getPopularActivity() {
            return popularActivity;
        }

        public void setPopularActivity(List<PopularActivity> popularActivity) {
            this.popularActivity = popularActivity;
        }

    }

    public class PopularActivity {

        @SerializedName("total_booked")
        @Expose
        private Integer totalBooked;
        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
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

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
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


    public class Recentlyadded {

        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
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
