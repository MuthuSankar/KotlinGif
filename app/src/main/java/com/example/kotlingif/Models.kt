package com.example.kotlingif

class Models {

    class HomeFeed(val data: List<data>)

    class data(val id: String, val type: String, val url: String, val slug: String, val bitly_gif_url: String, val images: ImagesResponse)

    data class ImagesResponse(val fixed_width: ImagesSizeResponse,
                              val fixed_height: ImagesSizeResponse,
                              val original: ImagesSizeResponse)

    data class ImagesSizeResponse(val url: String)
}