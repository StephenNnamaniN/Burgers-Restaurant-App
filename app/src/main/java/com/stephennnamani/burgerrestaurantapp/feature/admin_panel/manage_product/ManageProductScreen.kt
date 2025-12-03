package com.stephennnamani.burgerrestaurantapp.feature.admin_panel.manage_product

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.stephennnamani.burgerrestaurantapp.core.data.models.ProductCategory
import com.stephennnamani.burgerrestaurantapp.feature.component.BurgerSelectTextField
import com.stephennnamani.burgerrestaurantapp.feature.component.BurgerTextField
import com.stephennnamani.burgerrestaurantapp.feature.component.ErrorCard
import com.stephennnamani.burgerrestaurantapp.feature.component.LoadingCard
import com.stephennnamani.burgerrestaurantapp.feature.component.PrimaryButton
import com.stephennnamani.burgerrestaurantapp.feature.component.dialog.CategoryDialog
import com.stephennnamani.burgerrestaurantapp.feature.util.DisplayResult
import com.stephennnamani.burgerrestaurantapp.feature.util.MessageUtils
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import com.stephennnamani.burgerrestaurantapp.ui.theme.BorderIdle
import com.stephennnamani.burgerrestaurantapp.ui.theme.ButtonPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.Surface
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextSecondary
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    id: String?,
    navigateBack: () -> Unit
){
    val viewModel = koinViewModel<ManageProductViewModel>()
    val screenState = viewModel.screenState
    var showToast by remember { mutableStateOf("") }
    val isFormValid = viewModel.isFormValid
    val createProductState by viewModel.createProductState.collectAsState()
    var dropdownMenuOpened by remember { mutableStateOf(false) }
    val deleteProductState by viewModel.deleteProductState.collectAsState()

    MessageUtils.ShowToast(message = showToast)
    val context = LocalContext.current

    val productImageUploadState = viewModel.imageUploaderState


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.uploadProductImageToStorage(uri)
        }
    )
    LaunchedEffect(createProductState) {
        if (createProductState.isSuccess()) {
            showToast = "New product successfully added!"
            navigateBack()
            viewModel.resetCreateProductState()
        }
        if (createProductState.isError()) {
            showToast = createProductState.getErrorMessage()
        }
    }

    LaunchedEffect(deleteProductState) {
        if (deleteProductState.isSuccess()){
            showToast = "Product deleted successfully!"
            navigateBack()
            viewModel.resetDeleteProductState()
        }
        if (deleteProductState.isError()){
            showToast = deleteProductState.getErrorMessage()
        }
    }

    AnimatedVisibility(
        visible = screenState.isCategoryDialogOpen
    ) {
        CategoryDialog(
            categories = screenState.allCategories,
            onDismiss = viewModel::onCategoryDialogDismiss,
            onSelectedCategory = viewModel::onCategorySelected
        )
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (id == null) "New product" else "Edit Product",
                        fontFamily = oswaldVariableFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back arrow icon",
                            tint = IconPrimary
                        )
                    }
                },
                actions = {
                    id.takeIf { it != null }?.let {
                        Box {
                            IconButton(
                                onClick = { dropdownMenuOpened = true}
                            ) {
                                Icon(
                                    painter = painterResource(Resources.Icon.VerticalMenu),
                                    contentDescription = "Vertical menu icon",
                                    tint = IconPrimary
                                )
                            }
                            DropdownMenu(
                                expanded = dropdownMenuOpened,
                                onDismissRequest = { dropdownMenuOpened = false}
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            modifier = Modifier.size(14.dp),
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Delete icon",
                                            tint = IconPrimary
                                        )
                                    },
                                    text = { Text(text = "Delete", color = TextPrimary)
                                    },
                                    onClick = {
                                        dropdownMenuOpened = false
                                        viewModel.deleteProduct(
                                            productId = screenState.id
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(paddingValues)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = BorderIdle,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            enabled = productImageUploadState.isIdle()
                        ){
                            if (!productImageUploadState.isLoading()){
                                imagePickerLauncher.launch("image/*")
                            }
                        }
                        .background(SurfaceLight),
                    contentAlignment = Alignment.Center
                ) {
                    productImageUploadState.DisplayResult(
                        onIdle = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(Resources.Icon.Plus),
                                contentDescription = "Add icon",
                                tint = IconPrimary
                            )
                        },
                        onLoading = {
                            LoadingCard(modifier = Modifier.fillMaxSize())
                        },
                        onSuccess = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(
                                        context
                                    ).data(screenState.productImage)
                                        .crossfade(enable = true)
                                        .build(),
                                    contentDescription = "Product image",
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(
                                            top = 12.dp,
                                            end = 12.dp
                                        )
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ButtonPrimary)
                                        .clickable {
                                            viewModel.deleteProductImageFromStorage { isSuccess, message ->
                                               showToast = message
                                            }
                                        }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ){
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(Resources.Icon.Delete),
                                        contentDescription = "Delete icon",
                                        tint = IconPrimary
                                    )
                                }
                            }
                        },
                        onError = { message ->
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ErrorCard(message = message)
                                Spacer(modifier = Modifier.height(12.dp))

                                TextButton(
                                    onClick = {
                                        viewModel.updateImageState(RequestState.Idle)
                                    }
                                ) {
                                    Text(
                                        text = "Try again",
                                        fontSize = FontSize.SMALL,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    )
                }

                BurgerTextField(
                    value = screenState.title,
                    onValueChange = viewModel::updateTitle,
                    placeholder = "Title"
                )
                BurgerTextField(
                    modifier = Modifier.height(120.dp),
                    value = screenState.description,
                    onValueChange = viewModel::updateDescription,
                    placeholder = "Description",
                    expanded = true
                )
                BurgerSelectTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = screenState.selectedCategory?.title ?: "",
                    onClick = viewModel::onCategoryFieldClick,
                    placeholder = "Select Category"
                )
                BurgerTextField(
                    value = "${screenState.energyValue ?: ""}",
                    onValueChange = { viewModel.updateEnergyValue(it.toIntOrNull() ?: 0)},
                    placeholder = "Energy Value",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                BurgerTextField(
                    modifier = Modifier.height(80.dp),
                    value = screenState.allergyAdvice,
                    onValueChange = viewModel::updateAllergyAdvice,
                    placeholder = "Allergy Advice",
                    expanded = true,
                )
                BurgerTextField(
                    modifier = Modifier.height(80.dp),
                    value = screenState.ingredients,
                    onValueChange = viewModel::updateIngredients,
                    expanded = true,
                    placeholder = "Ingredients"
                )
                BurgerTextField(
                    value = if (screenState.price == 0.0) ""
                            else "${screenState.price}",
                    onValueChange = {value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null){
                            viewModel.updatePrice(value.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    placeholder = "Price",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            PrimaryButton(
                text = if (id == null) "Add New Product" else "Update Product",
                icon = if (id == null) painterResource(Resources.Icon.Plus)
                else painterResource(Resources.Icon.Checkmark),
                enabled = isFormValid && !createProductState.isLoading(),
                onClick = {
                    if (id != null){
                        viewModel.updateProductDetails()
                    } else {
                        viewModel.createNewProduct()
                    }
                }
            )
        }

    }
}