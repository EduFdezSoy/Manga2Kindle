package es.edufdezsoy.manga2kindle.network

import es.edufdezsoy.manga2kindle.M2kApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl(M2kApplication.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(M2kApiService::class.java)
}