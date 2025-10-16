package com.stephennnamani.burgerrestaurantapp.core.data.repoImpl

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import com.stephennnamani.burgerrestaurantapp.core.data.domain.CustomerRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Country
import com.stephennnamani.burgerrestaurantapp.core.data.models.Customer
import com.stephennnamani.burgerrestaurantapp.core.data.models.PhoneNumber
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await

class CustomerRepoImpl: CustomerRepository {

    override fun getCurrentUserId(): String? =
        FirebaseAuth.getInstance().currentUser?.uid


    override suspend fun createCustomer(
        user: FirebaseUser,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        try {
            val customerCollection = Firebase.firestore.collection("customer")
            val docRef = customerCollection.document(user.uid)
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                val customer = Customer(
                    id = user.uid,
                    firstName = user.displayName?.split(" ")?.firstOrNull() ?: "Unknown",
                    lastName = user.displayName?.split(" ")?.lastOrNull() ?: "Unknown",
                    email = user.email ?: "Unknown",
                    profilePictureUrl = user.photoUrl.toString()
                )
                docRef.set(customer).await()
            }
            onSuccess()
        }catch (e: Exception) {
            RequestState.Error("Failed to create customer: ${e.message}")
        }
    }

    override suspend fun readCustomerFlow(): Flow<RequestState<Customer>>  = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val dataBase = Firebase.firestore
                dataBase.collection("customer")
                    .document(userId)
                    .snapshots()
                    .collectLatest { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val postalCode = (documentSnapshot.get("postalCode") as? Long)?.toInt()
                            val phoneNumberMap = documentSnapshot.get("phoneNumber") as? Map<*, *>
                            val phoneNumber = phoneNumberMap?.let {
                                val dialCode = (it["CountryCode"] as? Long)?.toInt()
                                val number = it["number"] as? String

                                if (dialCode != null && number != null) {
                                    PhoneNumber(
                                        dialCode = dialCode,
                                        number = number
                                    )
                                } else {
                                    null
                                }
                            }

                            val countryMap = documentSnapshot.get("country") as? Map<*, *>
                            val country = countryMap?.let { map ->
                                val name = map["name"] as? String
                                val code = map["code"] as? String
                                val dialCode = (map["diaCode"] as? Long)?.toInt()
                                val flagUrl = map["flagUrl"] as? String
                                if (name != null && code != null && dialCode != null && flagUrl != null)
                                    Country(
                                        name = name,
                                        code = code,
                                        dialCode = dialCode,
                                        flagUrl = flagUrl
                                    )
                                else null
                            }

                            val customer = Customer(
                                id = documentSnapshot.id,
                                firstName = documentSnapshot.get("firstName") as String,
                                lastName = documentSnapshot.get("lastName") as String,
                                email = documentSnapshot.get("email") as String,
                                city = documentSnapshot.get("city") as String?,
                                postalCode = postalCode,
                                phoneNumber = phoneNumber,
                                address = documentSnapshot.get("address") as String?,
                                country = country,
                                profilePictureUrl = documentSnapshot.get("photoUrl") as String?
                            )
                            send(RequestState.Success(data = customer))
                        } else {
                            send(RequestState.Error("Queried customer document does not exist."))
                        }
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading customer information: ${e.message}"))
        }
    }

    override suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null){
                val firestore = Firebase.firestore
                val customerCollection = firestore.collection("customer")
                val existingCustomer = customerCollection
                    .document(customer.id)
                    .get().await()
                if (existingCustomer.exists()){
                    val phoneNumberMap = customer.phoneNumber?.let {
                        mapOf(
                            "CountryCode" to it.dialCode,
                            "number" to it.number
                        )
                    }

                    val countryMap = customer.country?.let {
                        mapOf(
                            "name" to it.name,
                            "code" to it.code,
                            "dialCode" to it.dialCode,
                            "flagUrl" to it.flagUrl
                        )
                    }

                    customerCollection
                        .document(customer.id)
                        .update(
                            mapOf(
                                "firstName" to customer.firstName,
                                "lastname" to customer.lastName,
                                "city" to customer.city,
                                "postalCode" to customer.postalCode,
                                "address" to customer.address,
                                "phoneNumber" to phoneNumberMap,
                                "country" to countryMap
                            )
                        ).await()
                    onSuccess()
                } else {
                    RequestState.Error("Customer document not found.")
                }
            } else {
                RequestState.Error("User not available")
            }
        } catch (e: Exception) {
            onError("Error while updating customer information: ${e.message}")
        }
    }

    override suspend fun updateProfilePictureUrl(url: String): RequestState<Unit>  = try {
        val uid = getCurrentUserId() ?: return RequestState.Error("User is not available.")
        Firebase.firestore.collection("customer")
            .document(uid)
            .update("profilePhotoUrl", url)
            .await()

        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null){
                val request = userProfileChangeRequest { photoUri = url.toUri() }
                user.updateProfile(request).await()
                user.reload().await()
            }
        } catch (_: Exception){ }
        RequestState.Success(Unit)
    }catch (e: Exception){
        RequestState.Error("Error while updating profile picture url: ${e.message}")
    }

    override suspend fun uploadProfilePhoto(
        localUrl: Uri,
        onProgress: (Float) -> Unit
    ): RequestState<String> = try {
        val uid = getCurrentUserId() ?: return RequestState.Error("User is not available")
        val storage = FirebaseStorage.getInstance().reference
        val ref = storage.child("user/$uid/profile/profile_${System.currentTimeMillis()}.jpg")
        val metadata = storageMetadata {
            contentType = "image/jpeg"
            setCustomMetadata("uploadedBy", uid)
        }
        val task = ref.putFile(localUrl, metadata)
        task.addOnProgressListener { snapshot ->
            val picture = if (snapshot.totalByteCount > 0) snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount else 0f
            onProgress(picture.coerceIn(0f, 1f))
        }.await()
        val url = ref.downloadUrl.await().toString()
        RequestState.Success(url)
    }catch (e: Exception){
        RequestState.Error("Upload failed: ${e.message}")
    }

    override suspend fun signOut(): RequestState<Unit> {
        return try {
            Firebase.auth.signOut()
            RequestState.Success(Unit)
        } catch (e: Exception){
            RequestState.Error("Error while signing out: ${e.message}")
        }
    }
}