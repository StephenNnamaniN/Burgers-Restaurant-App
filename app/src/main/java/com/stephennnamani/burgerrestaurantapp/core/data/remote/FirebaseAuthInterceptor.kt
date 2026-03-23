package com.stephennnamani.burgerrestaurantapp.core.data.remote

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class FirebaseAuthInterceptor(
    private val auth: FirebaseAuth
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val user = auth.currentUser
        val originalRequest = chain.request()
        if (user == null){
            return chain.proceed(originalRequest)
        }

        val token = try {
            val task = user.getIdToken(false)
            Tasks.await(task, 10, TimeUnit.SECONDS).token
        } catch (_: Exception){
            null
        }


        val updatedRequest = if (!token.isNullOrBlank()){
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else originalRequest

        return chain.proceed(updatedRequest)
    }
}