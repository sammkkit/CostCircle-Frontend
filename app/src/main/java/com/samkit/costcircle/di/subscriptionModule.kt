package com.samkit.costcircle.di

import androidx.appcompat.app.AppCompatActivity
import com.samkit.costcircle.core.utils.BiometricPromptManager
import com.samkit.costcircle.data.subscription.remote.SubscriptionApi
import com.samkit.costcircle.data.subscription.repository.SubscriptionRepository
import com.samkit.costcircle.ui.subscription.SubscriptionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val subscriptionModule = module{
    single<SubscriptionApi> {
        get<Retrofit>().create(SubscriptionApi::class.java)
    }

    // 2. Repository
    single {
        SubscriptionRepository(get(), get()) // Injects SubscriptionApi and SessionManager
    }

    viewModel { SubscriptionViewModel(get(), get()) }
}