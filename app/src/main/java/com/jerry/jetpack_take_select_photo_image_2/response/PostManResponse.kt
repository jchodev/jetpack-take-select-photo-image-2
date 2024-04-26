package com.jerry.jetpack_take_select_photo_image_2.response

import com.squareup.moshi.Json

data class PostManResponse (
    @field:Json(name = "url") val url: String?
)