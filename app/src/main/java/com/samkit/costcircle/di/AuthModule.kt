package com.samkit.costcircle.di

import com.samkit.costcircle.data.auth.remote.AuthApiService
import com.samkit.costcircle.data.auth.repository.AuthRepository
import com.samkit.costcircle.ui.screens.auth.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val authModule = module {

    single<AuthApiService> {
        get<Retrofit>().create(AuthApiService::class.java)
    }

    single {
        AuthRepository(
            api = get(),
            sessionManager = get()
        )
    }

    viewModel {
        LoginViewModel(
            authRepository = get(),
            sessionManager = get()
        )
    }
}
