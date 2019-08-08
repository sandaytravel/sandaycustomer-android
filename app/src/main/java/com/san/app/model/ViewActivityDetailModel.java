package com.san.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ViewActivityDetailModel {

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

    public class ActivityInformation {

        @SerializedName("activity_information_description")
        @Expose
        private String activityInformationDescription;

        public String getActivityInformationDescription() {
            return activityInformationDescription;
        }

        public void setActivityInformationDescription(String activityInformationDescription) {
            this.activityInformationDescription = activityInformationDescription;
        }

    }

    public class Basicdetails {

        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
        @SerializedName("activity_main")
        @Expose
        private Integer activityMain;
        @SerializedName("activity_whishlistId")
        @Expose
        private Integer activityWhishlistId;
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
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("category")
        @Expose
        private String category;
        @SerializedName("category_icon_fullsized")
        @Expose
        private String categoryIconFullsized;
        @SerializedName("category_icon_resized")
        @Expose
        private String categoryIconResized;
        @SerializedName("wishlist")
        @Expose
        private String wishlist;
        @SerializedName("subcategory")
        @Expose
        private String subcategory;
        @SerializedName("total_review")
        @Expose
        private Integer totalReview;
        @SerializedName("average_review")
        @Expose
        private Double averageReview;
        @SerializedName("confirm_days")
        @Expose
        private Integer confirmDays;
        @SerializedName("days_off")
        @Expose
        private ArrayList<String> daysOff;
        @SerializedName("date_notavailable")
        @Expose
        private ArrayList<String> dateNotavailable;



        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public Integer getActivityMain() {
            return activityMain;
        }

        public void setActivityMain(Integer activityMain) {
            this.activityMain = activityMain;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategoryIconFullsized() {
            return categoryIconFullsized;
        }

        public void setCategoryIconFullsized(String categoryIconFullsized) {
            this.categoryIconFullsized = categoryIconFullsized;
        }

        public String getCategoryIconResized() {
            return categoryIconResized;
        }

        public void setCategoryIconResized(String categoryIconResized) {
            this.categoryIconResized = categoryIconResized;
        }

        public String getWishlist() {
            return wishlist;
        }

        public void setWishlist(String wishlist) {
            this.wishlist = wishlist;
        }

        public String getSubcategory() {
            return subcategory;
        }

        public void setSubcategory(String subcategory) {
            this.subcategory = subcategory;
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

        public Integer getConfirmDays() {
            return confirmDays;
        }

        public void setConfirmDays(Integer confirmDays) {
            this.confirmDays = confirmDays;
        }

        public ArrayList<String> getDaysOff() {
            return daysOff;
        }

        public void setDaysOff(ArrayList<String> daysOff) {
            this.daysOff = daysOff;
        }

        public ArrayList<String> getDateNotavailable() {
            return dateNotavailable;
        }

        public void setDateNotavailable(ArrayList<String> dateNotavailable) {
            this.dateNotavailable = dateNotavailable;
        }
    }

    public class CancellationPolicy {

        @SerializedName("cancellation_policy_description")
        @Expose
        private String cancellationPolicyDescription;

        public String getCancellationPolicyDescription() {
            return cancellationPolicyDescription;
        }

        public void setCancellationPolicyDescription(String cancellationPolicyDescription) {
            this.cancellationPolicyDescription = cancellationPolicyDescription;
        }

    }

    public class Faqdetail {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("answer")
        @Expose
        private String answer;
        @SerializedName("isOpen")
        @Expose
        private boolean isOpen=false;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }
    }

    public class HowToUse {

        @SerializedName("how_to_use_description")
        @Expose
        private String howToUseDescription;

        public String getHowToUseDescription() {
            return howToUseDescription;
        }

        public void setHowToUseDescription(String howToUseDescription) {
            this.howToUseDescription = howToUseDescription;
        }

    }

    public class PackageQuantity {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("qtmain_id")
        @Expose
        private Integer qtmainId;
        @SerializedName("activity_packagemain_id")
        @Expose
        private Integer activity_packagemain_id;
        @SerializedName("activity_package_id")
        @Expose
        private Integer activityPackageId;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("minimum_quantity")
        @Expose
        private Integer minimumQuantity;
        @SerializedName("maximum_quantity")
        @Expose
        private String maximumQuantity;
        @SerializedName("actual_price")
        @Expose
        private String actualPrice;
        @SerializedName("display_price")
        @Expose
        private String displayPrice;
        @SerializedName("is_delete")
        @Expose
        private Integer isDelete;
        @SerializedName("item_count")
        @Expose
        private Integer item_count = 0;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getActivityPackageId() {
            return activityPackageId;
        }

        public void setActivityPackageId(Integer activityPackageId) {
            this.activityPackageId = activityPackageId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMinimumQuantity() {
            return minimumQuantity;
        }

        public void setMinimumQuantity(Integer minimumQuantity) {
            this.minimumQuantity = minimumQuantity;
        }

        public String getMaximumQuantity() {
            return maximumQuantity;
        }

        public void setMaximumQuantity(String maximumQuantity) {
            this.maximumQuantity = maximumQuantity;
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

        public Integer getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(Integer isDelete) {
            this.isDelete = isDelete;
        }

        public Integer getItem_count() {
            return item_count;
        }

        public void setItem_count(Integer item_count) {
            this.item_count = item_count;
        }

        public Integer getQtmainId() {
            return qtmainId;
        }

        public void setQtmainId(Integer qtmainId) {
            this.qtmainId = qtmainId;
        }

        public Integer getActivity_packagemain_id() {
            return activity_packagemain_id;
        }

        public void setActivity_packagemain_id(Integer activity_packagemain_id) {
            this.activity_packagemain_id = activity_packagemain_id;
        }
    }

    public class Packageoption {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("packagemain_id")
        @Expose
        private Integer packageMainId;
        @SerializedName("activitymain_id")
        @Expose
        private Integer activityMainId;
        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
        @SerializedName("package_title")
        @Expose
        private String packageTitle;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("actual_price")
        @Expose
        private String actualPrice;
        @SerializedName("display_price")
        @Expose
        private String displayPrice;
        @SerializedName("is_delete")
        @Expose
        private Integer isDelete;
        @SerializedName("free_voucher")
        @Expose
        private Integer freeVoucher;
        @SerializedName("validity")
        @Expose
        private String validity;
        @SerializedName("package_quantity")
        @Expose
        private List<PackageQuantity> packageQuantity = null;

        //@SerializedName("isExpand")
        //@Expose
        private boolean isExpand = false;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public String getPackageTitle() {
            return packageTitle;
        }

        public void setPackageTitle(String packageTitle) {
            this.packageTitle = packageTitle;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public Integer getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(Integer isDelete) {
            this.isDelete = isDelete;
        }

        public Integer getFreeVoucher() {
            return freeVoucher;
        }

        public void setFreeVoucher(Integer freeVoucher) {
            this.freeVoucher = freeVoucher;
        }

        public String getValidity() {
            return validity;
        }

        public void setValidity(String validity) {
            this.validity = validity;
        }

        public List<PackageQuantity> getPackageQuantity() {
            return packageQuantity;
        }

        public void setPackageQuantity(List<PackageQuantity> packageQuantity) {
            this.packageQuantity = packageQuantity;
        }

        public boolean isExpand() {
            return isExpand;
        }

        public void setExpand(boolean expand) {
            isExpand = expand;
        }

        public Integer getPackageMainId() {
            return packageMainId;
        }

        public void setPackageMainId(Integer packageMainId) {
            this.packageMainId = packageMainId;
        }

        public Integer getActivityMainId() {
            return activityMainId;
        }

        public void setActivityMainId(Integer activityMainId) {
            this.activityMainId = activityMainId;
        }
    }

    public class Payload {

        @SerializedName("basicdetails")
        @Expose
        private Basicdetails basicdetails;
        @SerializedName("what_to_expect")
        @Expose
        private WhatToExpect whatToExpect;
        @SerializedName("activity_information")
        @Expose
        private ActivityInformation activityInformation;
        @SerializedName("how_to_use")
        @Expose
        private HowToUse howToUse;
        @SerializedName("cancellation_policy")
        @Expose
        private CancellationPolicy cancellationPolicy;
        @SerializedName("faqdetail")
        @Expose
        private List<Faqdetail> faqdetail = null;
        @SerializedName("policydetail")
        @Expose
        private List<Policydetail> policydetail = null;
        @SerializedName("packageoptions")
        @Expose
        private List<Packageoption> packageoptions = null;
        @SerializedName("recentlyadded")
        @Expose
        private List<Recentlyadded> recentlyadded = null;
        @SerializedName("popularActivity")
        @Expose
        private List<PopularActivity> popularActivity = null;
        @SerializedName("reviews")
        @Expose
        private List<Review> reviews = null;


        public Basicdetails getBasicdetails() {
            return basicdetails;
        }

        public void setBasicdetails(Basicdetails basicdetails) {
            this.basicdetails = basicdetails;
        }

        public WhatToExpect getWhatToExpect() {
            return whatToExpect;
        }

        public void setWhatToExpect(WhatToExpect whatToExpect) {
            this.whatToExpect = whatToExpect;
        }

        public ActivityInformation getActivityInformation() {
            return activityInformation;
        }

        public void setActivityInformation(ActivityInformation activityInformation) {
            this.activityInformation = activityInformation;
        }

        public HowToUse getHowToUse() {
            return howToUse;
        }

        public void setHowToUse(HowToUse howToUse) {
            this.howToUse = howToUse;
        }

        public CancellationPolicy getCancellationPolicy() {
            return cancellationPolicy;
        }

        public void setCancellationPolicy(CancellationPolicy cancellationPolicy) {
            this.cancellationPolicy = cancellationPolicy;
        }

        public List<Faqdetail> getFaqdetail() {
            return faqdetail;
        }

        public void setFaqdetail(List<Faqdetail> faqdetail) {
            this.faqdetail = faqdetail;
        }

        public List<Policydetail> getPolicydetail() {
            return policydetail;
        }

        public void setPolicydetail(List<Policydetail> policydetail) {
            this.policydetail = policydetail;
        }

        public List<Packageoption> getPackageoptions() {
            return packageoptions;
        }

        public void setPackageoptions(List<Packageoption> packageoptions) {
            this.packageoptions = packageoptions;
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

        public List<Review> getReviews() {
            return reviews;
        }

        public void setReviews(List<Review> reviews) {
            this.reviews = reviews;
        }

    }

    public class Policydetail {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("icon_fullsized")
        @Expose
        private String icon_fullsized;
        @SerializedName("icon_resized")
        @Expose
        private String icon_resized;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon_fullsized() {
            return icon_fullsized;
        }

        public void setIcon_fullsized(String icon_fullsized) {
            this.icon_fullsized = icon_fullsized;
        }

        public String getIcon_resized() {
            return icon_resized;
        }

        public void setIcon_resized(String icon_resized) {
            this.icon_resized = icon_resized;
        }
    }

    public class PopularActivity {

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

    }


    public class WhatToExpect {

        @SerializedName("what_to_expect_description")
        @Expose
        private String whatToExpectDescription;

        public String getWhatToExpectDescription() {
            return whatToExpectDescription;
        }

        public void setWhatToExpectDescription(String whatToExpectDescription) {
            this.whatToExpectDescription = whatToExpectDescription;
        }

    }


    public class Review {

        @SerializedName("review_id")
        @Expose
        private Integer reviewId;
        @SerializedName("review_date")
        @Expose
        private String reviewDate;
        @SerializedName("activity_id")
        @Expose
        private Integer activityId;
        @SerializedName("rating")
        @Expose
        private Integer rating;
        @SerializedName("review")
        @Expose
        private String review;
        @SerializedName("customer_id")
        @Expose
        private Integer customerId;
        @SerializedName("customer_name")
        @Expose
        private String customerName;
        @SerializedName("profile_pic")
        @Expose
        private String profilePic;
        @SerializedName("review_images")
        @Expose
        private List<ReviewImage> reviewImages = null;

        public Integer getReviewId() {
            return reviewId;
        }

        public void setReviewId(Integer reviewId) {
            this.reviewId = reviewId;
        }

        public String getReviewDate() {
            return reviewDate;
        }

        public void setReviewDate(String reviewDate) {
            this.reviewDate = reviewDate;
        }

        public Integer getActivityId() {
            return activityId;
        }

        public void setActivityId(Integer activityId) {
            this.activityId = activityId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public List<ReviewImage> getReviewImages() {
            return reviewImages;
        }

        public void setReviewImages(List<ReviewImage> reviewImages) {
            this.reviewImages = reviewImages;
        }

    }

    public class ReviewImage {

        @SerializedName("fullsize_image")
        @Expose
        private String fullsizeImage;
        @SerializedName("resize_image")
        @Expose
        private String resizeImage;

        public String getFullsizeImage() {
            return fullsizeImage;
        }

        public void setFullsizeImage(String fullsizeImage) {
            this.fullsizeImage = fullsizeImage;
        }

        public String getResizeImage() {
            return resizeImage;
        }

        public void setResizeImage(String resizeImage) {
            this.resizeImage = resizeImage;
        }

    }


}