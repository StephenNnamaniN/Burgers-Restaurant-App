package com.stephennnamani.burgerrestaurantapp.core.data.repoImpl

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import com.stephennnamani.burgerrestaurantapp.core.data.domain.ProductRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class ProductRepoImpl: ProductRepository {
    private  fun DocumentSnapshot.toProduct(): Product {
        return Product(
            id = id,
            createdAt = getLong("createdAt") ?: 0L,
            title = getString("title").orEmpty(),
            description = getString("description").orEmpty(),
            category = getString("category").orEmpty(),
            allergyAdvice = getString("allergyAdvice").orEmpty(),
            energyValue = getLong("energyValue")?.toInt(),
            ingredients = getString("ingredients").orEmpty(),
            price = getDouble("price") ?: 0.0,
            productImage = getString("productImage").orEmpty(),
            isNew = getBoolean("new") ?: false,
            isPopular = getBoolean("popular") ?: false,
            isDiscounted = getBoolean("discounted") ?: false
        )
    }
    override fun readNewProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            send(RequestState.Loading)
            Firebase.firestore.collection("products")
                .whereEqualTo("new", true)
                .limit(5)
                .snapshots()
                .collectLatest { snapshots ->
                    val products = snapshots.documents
                        .map { it.toProduct() }
                        .map { it.copy(title = it.title.uppercase()) }
                    send(RequestState.Success(products))
                }
        }catch (e: Exception){
            send(RequestState.Error("Error reading new products: ${e.message}"))
        }
    }

    override fun readPopularProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            send(RequestState.Loading)
            Firebase.firestore.collection("products")
                .whereEqualTo("popular", true)
                .limit(5)
                .snapshots()
                .collectLatest { snapshots ->
                    val products = snapshots.documents
                        .map { it.toProduct() }
                        .map { it.copy(title = it.title.uppercase()) }
                    send(RequestState.Success(products))
                }
        }catch (e: Exception){
            send(RequestState.Error("Error reading popular products: ${e.message}"))
        }
    }

    override fun readDiscountedProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            send(RequestState.Loading)
            Firebase.firestore.collection("products")
                .whereEqualTo("discounted", true)
                .limit(5)
                .snapshots()
                .collectLatest { snapshots ->
                    val products = snapshots.documents
                        .map { it.toProduct() }
                        .map { it.copy(title = it.title.uppercase()) }
                    send(RequestState.Success(products))
                }
        }catch (e: Exception){
            send(RequestState.Error("Error reading discounted products: ${e.message}"))
        }
    }



    override fun readProductsByCategory(category: String): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            send(RequestState.Loading)
            Firebase.firestore.collection("products")
                .whereEqualTo("category", category)
                .limit(10)
                .snapshots()
                .collectLatest { snapshots ->
                    val products = snapshots.documents
                        .map { it.toProduct() }
                        .map { it.copy(title = it.title.uppercase()) }
                    send(RequestState.Success(products))
                }
        }catch (e: Exception){
            send(RequestState.Error("Error reading category products: ${e.message}"))
        }
    }

    override fun readProductById(productId: String): Flow<RequestState<Product>> = channelFlow {
        try {
            send(RequestState.Loading)
            Firebase.firestore.collection("products")
                .document(productId)
                .snapshots()
                .collectLatest { snapshots ->
                    if (!snapshots.exists()){
                        send(RequestState.Error("Product not found."))
                        return@collectLatest
                    }
                    val products = snapshots.toProduct()
                        .copy(title = snapshots.getString("title").orEmpty().uppercase())
                    send(RequestState.Success(products))
                }
        }catch (e: Exception){
            send(RequestState.Error("Error reading product details: ${e.message}"))
        }
    }
}