package com.shadi.assignment.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface MatchApiService {
    @GET("api/")
    suspend fun getMatches(@Query("results") results: Int, @Query("page") page: Int): Response<MatchApiResponse>
}

