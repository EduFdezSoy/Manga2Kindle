package es.edufdezsoy.manga2kindle.network

import android.content.Context
import com.squareup.moshi.Moshi
import es.edufdezsoy.manga2kindle.utils.SingletonHolder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiService : SingletonHolder<Manga2KindleService, Context?>({
    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    val client = OkHttpClient.Builder()
//        .addInterceptor(interceptor)
        .build()

    val moshi = Moshi.Builder()
        .add(AuthorArrayListMoshiAdapter())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(Manga2KindleService.API_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

    retrofit.create(Manga2KindleService::class.java)
})