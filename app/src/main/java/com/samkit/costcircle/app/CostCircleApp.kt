package com.samkit.costcircle.app

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.MobileAds
import com.google.firebase.messaging.FirebaseMessaging
import com.samkit.costcircle.data.auth.session.SessionManager
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.di.appModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CostCircleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        startKoin {
            androidContext(this@CostCircleApp)
            modules(appModules)
        }


    }
}
