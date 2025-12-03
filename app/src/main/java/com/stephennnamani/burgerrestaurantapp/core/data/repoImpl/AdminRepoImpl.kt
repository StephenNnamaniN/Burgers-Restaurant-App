package com.stephennnamani.burgerrestaurantapp.core.data.repoImpl

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.storage
import com.stephennnamani.burgerrestaurantapp.core.data.domain.AdminRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import java.lang.IllegalStateException
import java.util.UUID
import kotlin.String

class AdminRepoImpl(): AdminRepository {
    private fun DocumentSnapshot.toProduct(): Product {
        return Product(
                id = id,
                title = getString("title").orEmpty(),
                description = getString("description").orEmpty(),
                category = getString("category").orEmpty(),
                allergyAdvice = getString("allergyAdvice").orEmpty(),
                energyValue = getLong("energyValue")?.toInt(),
                ingredients = getString("ingredients").orEmpty(),
                price = getDouble("price") ?: 0.0,
                productImage = getString("productImage").orEmpty()
        )
    }
    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun uploadProductImageToStorage(imageUri: Uri): Result<String> {
        val currentUserId = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not authenticated."))
        return try {
            val fileName = UUID.randomUUID().toString()
            val storageRef = Firebase.storage.reference
                .child("users/$currentUserId/products/$fileName")

            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteProductImageFromStorage(downloadUrl: String): Result<Unit> {
        return try {
            val storagePath = Firebase.storage.getReferenceFromUrl(downloadUrl)
            storagePath.delete().await()
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun createNewProduct(product: Product) {
        Firebase.firestore
            .collection("products")
            .document(product.id)
            .set(product)
            .await()
    }

    override suspend fun updateProductThumbnail(
        productId: String,
        downloadUrl: String
    ): Result<Unit> {
        return try {
            val database = Firebase.firestore
            val productCollection = database.collection("products")
            val docRef = productCollection.document(productId)
            docRef.update("productImage", downloadUrl).await()
            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(
                IllegalStateException("Error while updating image thumbnail: ${e.message}")
            )
        }
    }

    override fun readLastTenProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val database = Firebase.firestore
                database.collection("products")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(10)
                    .snapshots()
                    .collectLatest { queryDocumentSnapshots ->
                        val products = queryDocumentSnapshots.documents.map { documentSnapshot ->
                            documentSnapshot.toProduct()
                        }
                        send(RequestState.Success(products))
                    }
            } else {
                send(RequestState.Error("User is not available"))
            }

        } catch (e: Exception) {
            send(RequestState.Error("Error reading products from database: ${e.message}"))
        }
    }

    override suspend fun readProductById(id: String): RequestState<Product> {
        return try {
            val productDocRef = Firebase.firestore.collection("products")
                .document(id)
                .get()
                .await()
            if (productDocRef.exists()) {
                val product = productDocRef.toProduct()
                RequestState.Success(product)
            } else {
                RequestState.Error("Product not found.")
            }
        } catch (e: Exception){
            RequestState.Error("Error reading selected product: ${e.message}")
        }
    }
}