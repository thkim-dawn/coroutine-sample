package co.kr.taehoon.coroutine_sample.util

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/v2/search/image")
    suspend fun getSearchImage(
        @Query("query") query: String,
        @Query("page") page: Int
    ): JsonElement
}