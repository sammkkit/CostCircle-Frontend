package com.samkit.costcircle.di

import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.common.remote.ApiClient
import com.samkit.costcircle.data.common.remote.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {

    single { SessionManager(androidContext()) }


    single {
        OkHttpClient.Builder()
            // THE FIX: Add your AuthInterceptor back
            .addInterceptor(AuthInterceptor(get()))
            .retryOnConnectionFailure(false)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Use BODY to see full error details
            })
            .build()
    }
    single<Retrofit> {
        ApiClient.create(
            okHttpClient = get()
        )
    }

}
