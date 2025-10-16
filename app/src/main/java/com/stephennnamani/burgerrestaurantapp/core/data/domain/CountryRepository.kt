package com.stephennnamani.burgerrestaurantapp.core.data.domain

import com.stephennnamani.burgerrestaurantapp.core.data.models.Country
import com.stephennnamani.burgerrestaurantapp.core.data.models.toCountryOrNull
import com.stephennnamani.burgerrestaurantapp.core.data.remote.RestCountriesApi
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.code

interface CountryRepository {
    suspend fun fetchCountries(): Flow<RequestState<List<Country>>>
}

class CountryRepositoryImpl(
    private val api: RestCountriesApi
): CountryRepository{
    override suspend fun fetchCountries(): Flow<RequestState<List<Country>>> = flow {
        try {
            emit(RequestState.Loading)
            val countries = withContext(Dispatchers.IO){
                api.getAll()
                    .mapNotNull { it.toCountryOrNull() }
                    .distinctBy { it.code }
                    .sortedBy { it.name }
            }
            emit(RequestState.Success(countries))
        } catch (e: Exception) {
            emit(RequestState.Error("Cannot access the API endpoint: ${e.message ?: "Unknown error"}"))
        }
    }

}
