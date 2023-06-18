package org.d3if3155.helloworld.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.d3if3155.MoMi.model.CategoryPic
import org.d3if3155.MoMi.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private val API_BASE_URL = Constants().API_BASE_URL
private val IMAGE_BASE_URL = Constants().IMAGE_BASE_URL

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(API_BASE_URL)
    .build()

interface CategoryPicApiService {
    @GET("categorypic.json")
    suspend fun getCategoryPic(): List<CategoryPic>
}

object CategoryPicApi {

    val service: CategoryPicApiService by lazy {
        retrofit.create(CategoryPicApiService::class.java)
    }

    fun getCategoryPicUrl(imageId: String): String {
        return "$IMAGE_BASE_URL$imageId?alt=media"
    }

}

enum class ApiStatus { LOADING, SUCCESS, FAILED }
