package com.stephennnamani.burgerrestaurantapp.feature.admin_panel.manage_product

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stephennnamani.burgerrestaurantapp.core.data.domain.AdminRepository
import com.stephennnamani.burgerrestaurantapp.core.data.models.Product
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategory
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ManageProductState(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val selectedCategory: ProductCategory? = null,
    val allCategories: List<ProductCategory> = emptyList(),
    val isCategoryDialogOpen: Boolean = false,
    val productImage: String = "",
    val energyValue: Int? = null,
    val allergyAdvice: String = "",
    val ingredients: String = "",
    val price: Double = 0.0,
    val isNew: Boolean = false,
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false
)

class ManageProductViewModel(
    private val adminRepository: AdminRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val productId = savedStateHandle.get<String>("id") ?: ""
    private var originalProduct: Product? = null
    var screenState by mutableStateOf(ManageProductState())
        private set
    var imageUploaderState: RequestState<Unit> by mutableStateOf(RequestState.Idle)
        private set

    private val _createProductState = MutableStateFlow<RequestState<Unit>>(RequestState.Idle)
    val createProductState = _createProductState.asStateFlow()

    private val _deleteProductState = MutableStateFlow<RequestState<Unit>>(RequestState.Idle)
    val deleteProductState = _deleteProductState.asStateFlow()

    val isFormValid: Boolean
        get() = screenState.title.isNotEmpty() &&
                screenState.description.isNotEmpty() &&
                screenState.productImage.isNotEmpty() &&
                screenState.selectedCategory != null &&
                screenState.price != 0.0

    init {
        screenState = screenState.copy(
            allCategories = ProductCategory.entries
        )
        if (productId.isNotEmpty()){
            viewModelScope.launch {
                when(val result = adminRepository.readProductById(productId)) {
                    is RequestState.Success -> {
                        val product = result.data

                        originalProduct = product
                        screenState = screenState.copy(
                            id = product.id,
                            title = product.title,
                            description = product.description,
                            productImage = product.productImage,
                            selectedCategory = mapCategory(product.category),
                            allergyAdvice = product.allergyAdvice,
                            ingredients = product.ingredients,
                            energyValue = product.energyValue,
                            price = product.price,
                            isNew = product.isNew,
                            isPopular = product.isPopular,
                            isDiscounted = product.isDiscounted

                        )
                        updateImageState(RequestState.Success(Unit))
                    }
                    is RequestState.Error -> {}
                    else -> Unit
                }
            }
        }
    }
    private fun mapCategory(categoryTitle: String): ProductCategory? {
        return ProductCategory.entries.firstOrNull() {
            it.title.equals(categoryTitle, ignoreCase = true)
        }
    }

    fun onCategoryFieldClick(){
        screenState = screenState.copy(isCategoryDialogOpen = true)
    }

    fun onCategoryDialogDismiss(){
        screenState = screenState.copy(isCategoryDialogOpen = false)
    }

    fun onCategorySelected(category: ProductCategory) {
        screenState = screenState.copy(
            selectedCategory = category,
            isCategoryDialogOpen = false
        )
    }

    fun updateTitle(value: String){
        screenState = screenState.copy(title = value)
    }
    fun updateDescription(value: String){
        screenState = screenState.copy(description = value)
    }

    fun updateAllergyAdvice(value: String){
        screenState = screenState.copy(allergyAdvice = value)
    }

    fun updateIngredients(value: String){
        screenState = screenState.copy(ingredients = value)
    }

    fun updateEnergyValue(value: Int?){
        screenState = screenState.copy(energyValue = value)
    }
    fun updatePrice(value: Double){
        screenState = screenState.copy(price = value)
    }


    fun updateImageState(value: RequestState<Unit>){
        imageUploaderState = value
    }
    fun updateProductImage(value: String) {
        screenState = screenState.copy(productImage = value)
    }

    fun updateIsNew(value: Boolean){
        screenState = screenState.copy(isNew = value)
    }

    fun updateIsPopular(value: Boolean){
        screenState = screenState.copy(isPopular = value)
    }

    fun updateIsDiscounted(value: Boolean){
        screenState = screenState.copy(isDiscounted = value)
    }

    fun uploadProductImageToStorage(imageUri: Uri?) {
        if (imageUri == null) {
            updateImageState(RequestState.Error("No image selected. Please choose an image to continue."))
            return
        }

        updateImageState(RequestState.Loading)

        viewModelScope.launch {
            val updateResult = adminRepository.uploadProductImageToStorage(imageUri)
            updateResult.onSuccess { downloadUrl ->

                updateProductImage(downloadUrl)
                updateImageState(RequestState.Success(Unit))
            }
                .onFailure { throwable ->
                    updateImageState(RequestState.Error(throwable.message ?: "Error while uploading image."))
                }
        }
    }

    fun deleteProductImageFromStorage(
        onResult: (Boolean, String) -> Unit
    ){
        val downloadUrl = screenState.productImage
        if (downloadUrl.isBlank()){
            onResult(false, "No image to delete.")
            return
        }

        viewModelScope.launch {
            val deleteResult = adminRepository.deleteProductImageFromStorage(downloadUrl)
            deleteResult.onSuccess {
                updateImageState(RequestState.Idle)
            }.onFailure { throwable ->
                val message = throwable.message ?: "Error deleting product image."
                onResult(false, message)
            }
        }
    }

    fun createNewProduct() {
        if (!isFormValid) {
            _createProductState.value = RequestState.Error("Please complete all required fields.")
            return
        }
        viewModelScope.launch {
            try {
                _createProductState.value = RequestState.Loading

                val productToCreate = Product(
                    id = screenState.id,
                    title = screenState.title,
                    description = screenState.description,
                    category = screenState.selectedCategory!!.title,
                    allergyAdvice = screenState.allergyAdvice,
                    energyValue = screenState.energyValue,
                    ingredients = screenState.ingredients,
                    price = screenState.price,
                    productImage = screenState.productImage,
                    isNew = screenState.isNew,
                    isPopular = screenState.isPopular,
                    isDiscounted = screenState.isDiscounted
                )
                adminRepository.createNewProduct(productToCreate)
                _createProductState.value = RequestState.Success(Unit)
            } catch (e: Exception){
                val message = e.message ?: "An unknown error occurred while creating a new product"
                _createProductState.value = RequestState.Error(message)
            }
        }
    }

    fun resetCreateProductState(){
        _createProductState.value = RequestState.Idle
    }

    fun updateProductDetails(){
        viewModelScope.launch {
            _createProductState.value = RequestState.Loading

            val base = originalProduct
            if (base == null) {
                _createProductState.value = RequestState.Error("No product loaded to update.")
                return@launch
            }

            val updatedProduct = base.copy(
                title = screenState.title,
                description = screenState.description,
                productImage = screenState.productImage,
                category = screenState.selectedCategory?.title ?: base.category,
                allergyAdvice = screenState.allergyAdvice,
                ingredients = screenState.ingredients,
                energyValue = screenState.energyValue,
                price = screenState.price,
                isNew = screenState.isNew,
                isPopular = screenState.isPopular,
                isDiscounted = screenState.isDiscounted
            )
            val result = adminRepository.updateProduct(updatedProduct)

            result.onSuccess {
                _createProductState.value = RequestState.Success(Unit)
            }
                .onFailure { throwable ->
                    _createProductState.value = RequestState.Error(
                        throwable.message ?: "Error updating product."
                    )
                }
        }
    }

    fun deleteProduct(productId: String){
        viewModelScope.launch {
            _deleteProductState.value = RequestState.Loading
            val result = adminRepository.deleteProduct(productId)
            result
                .onSuccess {
                    _deleteProductState.value = RequestState.Success(Unit)
                }
                .onFailure { throwable ->
                    _deleteProductState.value = RequestState.Error(
                        throwable.message ?: "Error deleting product"
                    )
                }
        }
    }

    fun resetDeleteProductState(){
        _deleteProductState.value = RequestState.Idle
    }

}