package com.san.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartViewListModel {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
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
        @SerializedName("activitymain_id")
        @Expose
        private Integer activityMainId;
        @SerializedName("activity_status")
        @Expose
        private String activityStatus;
        @SerializedName("activity_title")
        @Expose
        private String activityTitle;
        @SerializedName("activity_image")
        @Expose
        private String activityImage;
        @SerializedName("package_title")
        @Expose
        private String packageTitle;
        @SerializedName("free_voucher")
        @Expose
        private Integer freeVoucher;
        @SerializedName("booking_date")
        @Expose
        private String bookingDate;
        @SerializedName("package_id")
        @Expose
        private Integer packageId;
        @SerializedName("packagemain_id")
        @Expose
        private Integer packageMainId;
        @SerializedName("Quantity")
        @Expose
        private List<Quantity> quantity = null;
        @SerializedName("total_price")
        @Expose
        private Double totalPrice;

        @SerializedName("errors")
        @Expose
        private List<String> errors = null;

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public String getActivityStatus() {
            return activityStatus;
        }

        public void setActivityStatus(String activityStatus) {
            this.activityStatus = activityStatus;
        }

        public String getActivityTitle() {
            return activityTitle;
        }

        public void setActivityTitle(String activityTitle) {
            this.activityTitle = activityTitle;
        }

        public String getActivityImage() {
            return activityImage;
        }

        public void setActivityImage(String activityImage) {
            this.activityImage = activityImage;
        }

        public String getPackageTitle() {
            return packageTitle;
        }

        public void setPackageTitle(String packageTitle) {
            this.packageTitle = packageTitle;
        }

        public Integer getFreeVoucher() {
            return freeVoucher;
        }

        public void setFreeVoucher(Integer freeVoucher) {
            this.freeVoucher = freeVoucher;
        }

        public String getBookingDate() {
            return bookingDate;
        }

        public void setBookingDate(String bookingDate) {
            this.bookingDate = bookingDate;
        }

        public Integer getPackageId() {
            return packageId;
        }

        public void setPackageId(Integer packageId) {
            this.packageId = packageId;
        }

        public List<Quantity> getQuantity() {
            return quantity;
        }

        public void setQuantity(List<Quantity> quantity) {
            this.quantity = quantity;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public Integer getActivityMainId() {
            return activityMainId;
        }

        public void setActivityMainId(Integer activityMainId) {
            this.activityMainId = activityMainId;
        }

        public Integer getPackageMainId() {
            return packageMainId;
        }

        public void setPackageMainId(Integer packageMainId) {
            this.packageMainId = packageMainId;
        }
    }

    public class Quantity {

        @SerializedName("quantitymain_id")
        @Expose
        private Integer quantityMain_id;
        @SerializedName("quantity_id")
        @Expose
        private Integer quantity_id;
        @SerializedName("quantity_name")
        @Expose
        private String quantity_name;
        @SerializedName("actual_price")
        @Expose
        private String actualPrice;
        @SerializedName("display_price")
        @Expose
        private String displayPrice;
        @SerializedName("quantity")
        @Expose
        private Integer quantity;
        @SerializedName("total_price")
        @Expose
        private Double totalPrice;

        public Integer getQuantity_id() {
            return quantity_id;
        }

        public void setQuantity_id(Integer quantity_id) {
            this.quantity_id = quantity_id;
        }

        public String getQuantity_name() {
            return quantity_name;
        }

        public void setQuantity_name(String quantity_name) {
            this.quantity_name = quantity_name;
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

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Integer getQuantityMain_id() {
            return quantityMain_id;
        }

        public void setQuantityMain_id(Integer quantityMain_id) {
            this.quantityMain_id = quantityMain_id;
        }
    }

}