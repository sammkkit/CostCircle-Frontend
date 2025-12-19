package com.samkit.costcircle.di

import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.common.remote.ApiClient
import com.samkit.costcircle.data.common.remote.AuthInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {

    single { SessionManager(androidContext()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(get()))
            .build()
    }

    single<Retrofit> {
        ApiClient.create(
            okHttpClient = get()
        )
    }

}
