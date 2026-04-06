package com.stephennnamani.burgerrestaurantapp.core.di



import com.google.firebase.auth.FirebaseAuth
import com.paypal.android.corepayments.Environment
import com.stephennnamani.burgerrestaurantapp.BuildConfig
import com.stephennnamani.burgerrestaurantapp.R
import com.stephennnamani.burgerrestaurantapp.core.data.auth.GoogleUiClient
import com.stephennnamani.burgerrestaurantapp.core.data.domain.AdminRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CartRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CountryRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CountryRepositoryImpl
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.PaymentRepository
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.remote.FirebaseAuthInterceptor
import com.stephennnamani.burgerrestaurantapp.core.data.remote.PayPalWebCheckoutCoordinator
import com.stephennnamani.burgerrestaurantapp.core.data.remote.PaymentApi
import com.stephennnamani.burgerrestaurantapp.core.data.remote.RestCountriesApi
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.AdminRepoImpl
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.CartRepoImpl
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.CustomerRepoImpl
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.PaymentRepositoryImpl
import com.stephennnamani.burgerrestaurantapp.core.data.repoImpl.ProductRepoImpl
import com.stephennnamani.burgerrestaurantapp.feature.admin_panel.AdminPanelViewModel
import com.stephennnamani.burgerrestaurantapp.feature.admin_panel.manage_product.ManageProductViewModel
import com.stephennnamani.burgerrestaurantapp.feature.auth.AuthViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.HomeViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.cart.CartDeliveryViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.cart.CartViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.categories.FoodMenuViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.product_overview.ProductOverviewViewModel
import com.stephennnamani.burgerrestaurantapp.feature.payment.paypal.CheckoutViewModel
import com.stephennnamani.burgerrestaurantapp.feature.payment.paypal.PayPalCheckoutViewModel
import com.stephennnamani.burgerrestaurantapp.feature.product_details.ProductDetailsViewModel
import com.stephennnamani.burgerrestaurantapp.feature.profile.ProfileViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.Single
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
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
    single<CartRepository> { CartRepoImpl(get(), get()) }

    single<PaymentApi> { get<Retrofit>(named("paymentsRetrofit")).create(PaymentApi::class.java) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { ManageProductViewModel(get(), get()) }
    viewModel { AdminPanelViewModel(get()) }
    viewModel { ProductOverviewViewModel(get(), get()) }
    viewModel { ProductDetailsViewModel(get(), get(), get()) }
    viewModel { CartViewModel(get()) }
    viewModel { FoodMenuViewModel(get(), get(), get()) }

    viewModel { CheckoutViewModel(get()) }
    viewModel { PayPalCheckoutViewModel(get(), get(), get()) }
    viewModel { CartDeliveryViewModel(get()) }

    single {
        GoogleUiClient(
            context = androidContext(),
            auth = get(),
            serverClient = androidContext().getString(R.string.default_web_client_id)
        )
    }

    single {
        PayPalWebCheckoutCoordinator(
            appContext = androidContext(),
            environment = Environment.SANDBOX
        )
    }

    single (named("paymentsOkHttp")){
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(FirebaseAuthInterceptor(get()))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single(named("paymentsRetrofit")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.PAYMENTS_BASE_URL)
            .client(get(named("paymentsOkHttp")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}