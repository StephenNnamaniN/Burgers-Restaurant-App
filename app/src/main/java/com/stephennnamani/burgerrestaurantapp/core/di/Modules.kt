package com.stephennnamani.burgerrestaurantapp.core.di



import com.google.firebase.auth.FirebaseAuth
import com.stephennnamani.burgerrestaurantapp.R
import com.stephennnamani.burgerrestaurantapp.core.data.auth.GoogleUiClient
import com.stephennnamani.burgerrestaurantapp.core.data.domain.AdminRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CountryRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CountryRepositoryImpl
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.remote.RestCountriesApi
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.AdminRepoImpl
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.CustomerRepoImpl
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.ProductRepoImpl
import com.stephennnamani.burgerrestaurantapp.feature.admin_panel.AdminPanelViewModel
import com.stephennnamani.burgerrestaurantapp.feature.admin_panel.manage_product.ManageProductViewModel
import com.stephennnamani.burgerrestaurantapp.feature.auth.AuthViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.HomeViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.product_overview.ProductOverviewViewModel
import com.stephennnamani.burgerrestaurantapp.feature.profile.ProfileViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://restcountries.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<RestCountriesApi> { get<Retrofit>().create(RestCountriesApi::class.java) }
    single<CountryRepository> { CountryRepositoryImpl(get()) }

    single<FirebaseAuth> { FirebaseAuth.getInstance() }

    single<CustomerRepository> { CustomerRepoImpl() }
    single<AdminRepository> { AdminRepoImpl() }
    single<ProductRepository> { ProductRepoImpl() }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { ManageProductViewModel(get(), get()) }
    viewModel { AdminPanelViewModel(get()) }
    viewModel { ProductOverviewViewModel(get()) }

    single {
        GoogleUiClient(
            context = androidContext(),
            auth = get(),
            serverClient = androidContext().getString(R.string.default_web_client_id)
        )
    }
}