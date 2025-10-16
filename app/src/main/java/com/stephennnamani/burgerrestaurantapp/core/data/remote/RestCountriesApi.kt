package com.stephennnamani.burgerrestaurantapp.core.data.remote

import com.stephennnamani.burgerrestaurantapp.core.data.models.RestCountriesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RestCountriesApi {
    @GET("v3.1/all")
    suspend fun getAll(
        @Query("fields") fields: String = "name,idd,flags,cca2"
    ): List<RestCountriesDto>
}