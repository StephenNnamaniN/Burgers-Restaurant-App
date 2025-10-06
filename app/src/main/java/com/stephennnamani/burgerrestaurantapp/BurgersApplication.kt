package com.stephennnamani.burgerrestaurantapp

import android.app.Application
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.stephennnamani.burgerrestaurantapp.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class BurgersApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BurgersApplication)
            modules(appModule)
        }

    }
}