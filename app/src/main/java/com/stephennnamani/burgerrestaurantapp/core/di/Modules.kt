package com.stephennnamani.burgerrestaurantapp.core.di



import com.google.firebase.auth.FirebaseAuth
import com.stephennnamani.burgerrestaurantapp.R

import com.stephennnamani.burgerrestaurantapp.core.data.auth.GoogleUiClient
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.CustomerRepoImpl
import com.stephennnamani.burgerrestaurantapp.feature.auth.AuthViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<FirebaseAuth> { FirebaseAuth.getInstance() }

    single<CustomerRepository> { CustomerRepoImpl() }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }

    single {
        GoogleUiClient(
            context = androidContext(),
            auth = get(),
            serverClient = androidContext().getString(R.string.default_web_client_id)
        )
    }
}