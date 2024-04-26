package com.jerry.jetpack_take_select_photo_image_2.api

import com.jerry.jetpack_take_select_photo_image_2.response.PostManResponse
import okhttp3.MultipartBody
import retrofit2.http.Body

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {

    @Multipart
    @POST("/post")
    fun formWithImage(
        @Part("name") name: String,
        @Part("image") image: MultipartBody.Part
    )  :retrofit2.Response<String>

    //
    @POST("/post")
    suspend fun formWithImage2(@Body body: MultipartBody): retrofit2.Response<PostManResponse>
}