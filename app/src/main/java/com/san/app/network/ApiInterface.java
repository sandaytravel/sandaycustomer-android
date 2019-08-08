package com.san.app.network;


import com.san.app.model.ActivityListModel;
import com.san.app.model.CartViewListModel;
import com.san.app.model.CountryListModel;
import com.san.app.model.MyOrderListModel;
import com.san.app.model.NotificationListModel;
import com.san.app.model.SearchCityArractionListModel;
import com.san.app.model.ViewActivityDetailModel;
import com.san.app.model.ViewCityDetailModel;
import com.san.app.model.WishListModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(@Field("email") String email,
                             @Field("password") String password,
                             @Field("device_token") String device_token,
                             @Field("device_type") String device_type,
                             @Field("language_id") String language_id);

    @FormUrlEncoded
    @POST("facebooklogin")
    Call<ResponseBody> facebooklogin(@Field("email") String email,
                                     @Field("name") String name,
                                     @Field("phone") String phone,
                                     @Field("profile_pic") String profile_pic,
                                     @Field("facebook_id") String facebook_id,
                                     @Field("device_token") String device_token,
                                     @Field("device_type") String device_type,
                                     @Field("language_id") String language_id);


    @FormUrlEncoded
    @POST("customer_registration")
    Call<ResponseBody> customer_registration(@Field("name") String name,
                                             @Field("email") String email,
                                             @Field("password") String password,
                                             @Field("device_token") String device_token,
                                             @Field("device_type") String device_type,
                                             @Field("birth_date") String birth_date,
                                             @Field("language_id") String language_id);

    @FormUrlEncoded
    @POST("forgotpassword")
    Call<ResponseBody> forgotpassword(@Field("email") String email,
                                      @Field("language_id") String language_id);

    @GET("explore")
    Call<ResponseBody> exploreHome(@Header("auth_token") String auth_token,
                                   @QueryMap Map<String, String> options);

    @POST("search")
    Call<SearchCityArractionListModel> searchCityOrAttraction(@QueryMap Map<String, String> options);

    @FormUrlEncoded
    @POST("destinations")
    Call<ResponseBody> destinations(@Field("continent") String continent,
                                    @QueryMap Map<String, String> options);

    @GET("viewcity")
    Call<ViewCityDetailModel> viewcityDetail(@QueryMap Map<String, String> options);

    @POST("activitylist")
    Call<ActivityListModel> activitylist(@QueryMap Map<String, String> options);

    @GET("viewactivity")
    Call<ViewActivityDetailModel> viewactivityDetail(@Header("auth_token") String auth_token,
                                                     @QueryMap Map<String, String> options);


    @GET("viewcart")
    Call<CartViewListModel> viewcartList(@Header("auth_token") String auth_token,
                                         @QueryMap Map<String, String> options);

    @POST("add_remove_whishlist")
    Call<ResponseBody> add_remove_whishlist(@Header("auth_token") String auth_token,
                                            @QueryMap Map<String, String> options);

    @GET("whishlist")
    Call<WishListModel> whishlist(@Header("auth_token") String auth_token,
                                  @QueryMap Map<String, String> options);


    @Multipart
    @POST("updateprofilepic")
    Call<ResponseBody> updateprofilepic(@Header("auth_token") String auth_token,
                                        @Part MultipartBody.Part profile_pic);

    @POST("updateprofile")
        //edit my details
    Call<ResponseBody> updateprofile(@Header("auth_token") String auth_token,
                                     @QueryMap Map<String, String> options);

    @POST("change_password")
        //change password
    Call<ResponseBody> changepassword(@Header("auth_token") String auth_token,
                                      @QueryMap Map<String, String> options);


    @POST("addtocart")
        //addtocart
    Call<ResponseBody> addtocart(@Header("auth_token") String auth_token,
                                 @QueryMap Map<String, String> options);

    @POST("editcart")
        //editcart
    Call<ResponseBody> editcart(@Header("auth_token") String auth_token,
                                @QueryMap Map<String, String> options);

    @POST("deletecart")
        //delete cart itme
    Call<ResponseBody> deletecartItem(@Header("auth_token") String auth_token,
                                      @QueryMap Map<String, String> options);


    @POST("placeorder")
    Call<ResponseBody> placeorder(@Header("auth_token") String auth_token,
                                  @QueryMap HashMap<String, String> options);

    @GET("vieworder")
    Call<MyOrderListModel> vieworder(@Header("auth_token") String auth_token,
                                     @QueryMap HashMap<String, String> options);

    @FormUrlEncoded
    @POST("customerbookingdetails")
    Call<ResponseBody> customerbookingdetails(@Header("auth_token") String auth_token,
                                              @Field("order_id") String order_id,
                                              @Field("language_id") String language_id);

    @Multipart
    @POST("reviewactivity")
        //post review of the acitivyt
    Call<ResponseBody> reviewSubmitPOST(@Header("auth_token") String auth_token,
                                        @Part("activity_id") RequestBody activity_id,
                                        @Part("order_id") RequestBody order_id,
                                        @Part("rating") RequestBody rating,
                                        @Part("review") RequestBody review,
                                        @Part("language_id") RequestBody language_id,
                                        @Part ArrayList<MultipartBody.Part> images);

    @GET("activityreview")
    Call<ResponseBody> allActivityReview(@QueryMap Map<String, String> options);

    @GET("about-us")
    Call<ResponseBody> aboutUs();

    @GET("customernotification")
    Call<NotificationListModel> customernotification(@Header("auth_token") String auth_token,
                                                     @QueryMap Map<String, String> options);

    @GET("getcountrylist")
    Call<CountryListModel> getcountrylist(@QueryMap Map<String, String> options);


    @POST("checkpaymentstatus")
    Call<ResponseBody> checkpaymentstatus(@Header("auth_token") String auth_token,
                                          @QueryMap Map<String, String> options);

    @POST("languageselect")
    Call<ResponseBody> languageselect(@Header("auth_token") String auth_token,
                                          @QueryMap Map<String, String> options);

    @POST("checkproceedtopay")  // check valid activity of not
    Call<ResponseBody> checkproceedtopay(@Header("auth_token") String auth_token,
                                          @QueryMap Map<String, String> options);

    @POST("proceedtovalid")  // check proceedtovalid
    Call<ResponseBody> proceedtovalid(@Header("auth_token") String auth_token,
                                         @QueryMap Map<String, String> options);

    @GET("logout")
    Call<ResponseBody> logout(@Header("auth_token") String auth_token,
                              @QueryMap Map<String, String> options);


}
