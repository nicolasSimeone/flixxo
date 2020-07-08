package com.flixxo.apps.flixxoapp.repositories.remote.service

import android.net.Uri
import com.flixxo.apps.flixxoapp.model.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiClient {

    @Headers("no-auth: no-auth")
    @POST("auth/login")
    fun login(@Body body: LoginBody): Deferred<Response<LoginResponse>>

    @Headers("no-auth: no-auth")
    @POST("auth/loginExternal")
    fun loginFacebook(@Body body: LoginFacebookBody): Deferred<Response<LoginResponse>>

    @Headers("no-auth: no-auth")
    @POST("auth/register")
    fun register(@Body body: RegisterBody): Deferred<Response<RegisterResponse>>

    @GET("contents/top/byuser")
    fun getTopContent(@Query(value = "contentType") contentType: String): Deferred<Response<ContentResponse>>

    @GET("me/balance")
    fun getBalance(): Deferred<Response<BalanceResponse>>

    @GET("contents/{contentUUID}")
    fun getContentDetail(@Path("contentUUID") id: String): Deferred<Response<Content>>

    @GET("search/contents")
    fun getSearch(@Query("searchData") word: String?, @Query("filters") filters: String?, @Query("contentType") contentType: String): Deferred<Response<ContentResponse>>

    @GET("categories")
    fun getCategories(): Deferred<Response<List<Category>>>

    @GET("contents/top/bycategory/{categoryId}")
    fun getContentsByCategoryId(@Path("categoryId") id: Int, @Query(value = "contentType") contentType: String): Deferred<Response<ContentResponse>>

    @POST("mailer")
    fun sendEmail(@Body body: MailerBody): Deferred<Response<MailerResponse>>

    @POST("payments/content")
    fun payContent(@Body body: PaymentsContentBody): Deferred<Response<PaymentsContentResponse>>

    @GET("me/contents/purchased")
    fun getContentPurchased(@Query(value = "contentType") contentType: String): Deferred<Response<List<Content>>>

    @Headers("no-auth: no-auth")
    @POST("auth/forgotcode")
    fun forgotPassword(@Body body: ForgotPasswordBody): Deferred<Response<SuccessResponse>>

    @Headers("no-auth: no-auth")
    @POST("auth/resetpassword")
    fun resetPassword(@Body body: ResetPasswordBody): Deferred<Response<SuccessResponse>>

    @GET("series/{serieUUID}")
    fun getSerieDetail(@Path("serieUUID") id: String): Deferred<Response<Series>>

    @GET("advertisements/reward")
    fun getAdvertisement(): Deferred<Response<Double>>

    @PATCH("me/profile/")
    fun userProfile(@Body body: Profile): Deferred<Response<User>>

    @GET("countries")
    fun getCountries(): Deferred<Response<List<Country>>>

    @GET("advertisements")
    fun getAdPlayer(): Deferred<Response<AdvertisementResponse>>

    @POST("advertisements")
    fun adWatched(@Body body: AdWatchedBody): Deferred<Response<AdWatchedResponse>>

    @POST("me/followed_categories")
    fun followedCategories(@Body body: List<Int>): Deferred<Response<List<Category>>>

    @Headers("no-auth: no-auth")
    @POST("auth/confirm")
    fun confirmCode(@Body body: Confirm): Deferred<Response<SuccessResponse>>

    @Headers("no-auth: no-auth")
    @POST("auth/confirm/sms")
    fun confirmSMS(@Body body: Confirm): Deferred<Response<SuccessResponse>>

    @Headers("no-auth: no-auth")
    @POST("auth/resendcode/mail")
    fun resendCodeMail(@Body body: EmailBody): Deferred<Response<SuccessResponse>>

    @Headers("no-auth: no-auth")
    @POST("auth/resendcode/sms")
    fun resendCodeSMS(@Body body: EmailBody): Deferred<Response<ResponseResendCodeSms>>

    @GET("me")
    fun getUserStatus(): Deferred<Response<User>>

    @GET("me/followers")
    fun getFollowers(): Deferred<Response<List<Author>>>

    @GET("me/followeds")
    fun getFollowings(): Deferred<Response<List<Author>>>

    @Headers("no-auth: no-auth")
    @GET("https://s3.amazonaws.com/test.pro.flixxo.com/mobile/messages/load_messages_en.json")
    fun getLoadMessagesEn(): Deferred<Response<LoadMessages>>

    @Headers("no-auth: no-auth")
    @GET("https://s3.amazonaws.com/test.pro.flixxo.com/mobile/messages/load_messages_es.json")
    fun getLoadMessagesEs(): Deferred<Response<LoadMessages>>

    @POST("follow/{userId}")
    fun followById(@Path("userId") id: String): Deferred<Response<List<Author>>>

    @DELETE("follow/{userId}")
    fun unfollowById(@Path("userId") id: String): Deferred<Response<List<Author>>>

    @GET("me/cashin/wallet")
    fun getToken(): Deferred<Response<String>>

    @POST("me/cashin/wallet")
    fun createToken(): Deferred<Response<String>>


    @POST("auth/changePlainPassword")
    fun changePassword(@Body body: ChangePassword): Deferred<Response<SuccessResponse>>

    @DELETE("me/profile/avatar")
    fun deletePhoto(): Deferred<Response<SuccessResponse>>

    @Multipart
    @POST("me/profile/avatar")
    fun changePhoto(@Part image: MultipartBody.Part): Deferred<Response<Profile>>

    @GET("me/clientkeys/")
    fun getUserKeys(): Deferred<Response<List<ClientKey>>>

    @POST("me/clientkeys")
    fun sendUserKey(@Body key: RequestBody): Deferred<Response<List<ClientKey>>>

    @GET("langs")
    fun getLanguages(): Deferred<Response<List<Language>>>

    @GET("contents/{contentUUID}/torrentFile")
    fun getTorrentFile(@Path("contentUUID") uuid: String): Deferred<Response<NewTorrentFile>>

    @GET("me/followed_categories")
    fun getFollowedCategories() : Deferred<Response<List<Category>>>
}
