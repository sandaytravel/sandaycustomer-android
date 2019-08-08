package com.san.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyOrderListModel {

    @SerializedName("payload")
    @Expose
    private List<Payload> payload = null;
    @SerializedName("page")
    @Expose
    private Integer page;
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
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

    public class Order {

        @SerializedName("order_id")
        @Expose
        private Integer orderId;
        @SerializedName("customer_id")
        @Expose
        private Integer customerId;
        @SerializedName("order_number")
        @Expose
        private String orderNumber;
        @SerializedName("order_payment_status")
        @Expose
        private String orderPaymentStatus;
        @SerializedName("booking_date")
        @Expose
        private String bookingDate;
        @SerializedName("status_date")
        @Expose
        private String statusDate;
        @SerializedName("participation_date")
        @Expose
        private String participationDate;
        @SerializedName("order_status")
        @Expose
        private Integer orderStatus;
        @SerializedName("total_price")
        @Expose
        private String totalPrice;
        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
        @SerializedName("activity_name")
        @Expose
        private String activityName;
        @SerializedName("voucher_url")
        @Expose
        private String voucherUrl;
        @SerializedName("voucher_number")
        @Expose
        private String voucherNumber;
        @SerializedName("is_redeem")
        @Expose
        private Integer isRedeem;
        @SerializedName("voucher_qrcode")
        @Expose
        private String voucherQrcode;
        @SerializedName("is_review_given")
        @Expose
        private String isReviewGiven;
        @SerializedName("activity_image")
        @Expose
        private String activityImage;
        @SerializedName("package_title")
        @Expose
        private String packageTitle;
        @SerializedName("package_id")
        @Expose
        private Integer packageId;
        @SerializedName("packagequantity")
        @Expose
        private List<Packagequantity> packagequantity = null;
        @SerializedName("notes")
        @Expose
        private List<Note> notes = null;

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public String getOrderPaymentStatus() {
            return orderPaymentStatus;
        }

        public void setOrderPaymentStatus(String orderPaymentStatus) {
            this.orderPaymentStatus = orderPaymentStatus;
        }

        public String getBookingDate() {
            return bookingDate;
        }

        public void setBookingDate(String bookingDate) {
            this.bookingDate = bookingDate;
        }

        public String getStatusDate() {
            return statusDate;
        }

        public void setStatusDate(String statusDate) {
            this.statusDate = statusDate;
        }

        public String getParticipationDate() {
            return participationDate;
        }

        public void setParticipationDate(String participationDate) {
            this.participationDate = participationDate;
        }

        public Integer getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getVoucherUrl() {
            return voucherUrl;
        }

        public void setVoucherUrl(String voucherUrl) {
            this.voucherUrl = voucherUrl;
        }

        public String getVoucherNumber() {
            return voucherNumber;
        }

        public void setVoucherNumber(String voucherNumber) {
            this.voucherNumber = voucherNumber;
        }

        public Integer getIsRedeem() {
            return isRedeem;
        }

        public void setIsRedeem(Integer isRedeem) {
            this.isRedeem = isRedeem;
        }

        public String getVoucherQrcode() {
            return voucherQrcode;
        }

        public void setVoucherQrcode(String voucherQrcode) {
            this.voucherQrcode = voucherQrcode;
        }

        public String getIsReviewGiven() {
            return isReviewGiven;
        }

        public void setIsReviewGiven(String isReviewGiven) {
            this.isReviewGiven = isReviewGiven;
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

        public Integer getPackageId() {
            return packageId;
        }

        public void setPackageId(Integer packageId) {
            this.packageId = packageId;
        }

        public List<Packagequantity> getPackagequantity() {
            return packagequantity;
        }

        public void setPackagequantity(List<Packagequantity> packagequantity) {
            this.packagequantity = packagequantity;
        }

        public List<Note> getNotes() {
            return notes;
        }

        public void setNotes(List<Note> notes) {
            this.notes = notes;
        }

    }

    public class Packagequantity {

        @SerializedName("quantity")
        @Expose
        private Integer quantity;
        @SerializedName("quantity_id")
        @Expose
        private Integer quantityId;
        @SerializedName("quantity_name")
        @Expose
        private String quantityName;
        @SerializedName("quantity_price")
        @Expose
        private String quantityPrice;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getQuantityId() {
            return quantityId;
        }

        public void setQuantityId(Integer quantityId) {
            this.quantityId = quantityId;
        }

        public String getQuantityName() {
            return quantityName;
        }

        public void setQuantityName(String quantityName) {
            this.quantityName = quantityName;
        }

        public String getQuantityPrice() {
            return quantityPrice;
        }

        public void setQuantityPrice(String quantityPrice) {
            this.quantityPrice = quantityPrice;
        }

    }

    public class Note {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("sender_id")
        @Expose
        private Integer senderId;
        @SerializedName("receiver_id")
        @Expose
        private Integer receiverId;
        @SerializedName("order_id")
        @Expose
        private Integer orderId;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("created_at")
        @Expose
        private String createdAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSenderId() {
            return senderId;
        }

        public void setSenderId(Integer senderId) {
            this.senderId = senderId;
        }

        public Integer getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Integer receiverId) {
            this.receiverId = receiverId;
        }

        public Integer getOrderId() {
            return orderId;
        }

        public void setOrderId(Integer orderId) {
            this.orderId = orderId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    public class Payload {

        @SerializedName("webviewurl")
        @Expose
        private String webviewurl;
        @SerializedName("transaction_id")
        @Expose
        private Integer transactionId;
        @SerializedName("transaction_number")
        @Expose
        private String transactionNumber;
        @SerializedName("payment_status")
        @Expose
        private String paymentStatus;
        @SerializedName("transaction_date")
        @Expose
        private String transactionDate;
        @SerializedName("total_amount")
        @Expose
        private Double totalAmount;
        @SerializedName("orders")
        @Expose
        private List<Order> orders = null;

        public String getWebviewurl() {
            return webviewurl;
        }

        public void setWebviewurl(String webviewurl) {
            this.webviewurl = webviewurl;
        }

        public Integer getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(Integer transactionId) {
            this.transactionId = transactionId;
        }

        public String getTransactionNumber() {
            return transactionNumber;
        }

        public void setTransactionNumber(String transactionNumber) {
            this.transactionNumber = transactionNumber;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public List<Order> getOrders() {
            return orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

    }

}

