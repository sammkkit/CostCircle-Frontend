package com.samkit.costcircle.di

import androidx.appcompat.app.AppCompatActivity
import com.samkit.costcircle.core.utils.BiometricPromptManager
import org.koin.dsl.module

val securityModule = module{
    factory { (activity: AppCompatActivity) ->
        BiometricPromptManager(activity)
    }
}