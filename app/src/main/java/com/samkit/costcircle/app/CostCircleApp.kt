package com.samkit.costcircle.app

import android.app.Application
import com.samkit.costcircle.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CostCircleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CostCircleApp)
            modules(appModules)
        }
    }
}
