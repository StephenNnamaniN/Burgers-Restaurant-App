package com.stephennnamani.burgerrestaurantapp.feature.product_details

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.stephennnamani.burgerrestaurantapp.feature.component.InfoCard
import com.stephennnamani.burgerrestaurantapp.feature.component.LoadingCard
import com.stephennnamani.burgerrestaurantapp.feature.component.PrimaryButton
import com.stephennnamani.burgerrestaurantapp.feature.component.QuantityStepper
import com.stephennnamani.burgerrestaurantapp.feature.component.dialog.AddMoreToCartDialog
import com.stephennnamani.burgerrestaurantapp.feature.util.DisplayResult
import com.stephennnamani.burgerrestaurantapp.ui.theme.BorderIdle
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandBrown
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandYellow
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.Surface
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceDark
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    navigateBack: () -> Unit,
    navigateToCart: () -> Unit,
    navigateToCheckout: (Double) -> Unit,
    navigateToMenu: () -> Unit
){
    val viewModel = koinViewModel<ProductDetailsViewModel>()
    val productState by viewModel.product.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalPlatformContext.current
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProductDetailsEvent.NavigateToCheckout -> event.amount?.let { navigateToCheckout(it) }
                is ProductDetailsEvent.ShowMessage ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    if (uiState.showSuggestedDialog) {
        AddMoreToCartDialog(
            suggestedProducts = uiState.suggestedProducts,
            initialItemTotal = uiState.addedCartTotal,
            onDismiss = viewModel::dismissSuggestedDialog,
            onProductClick = {},
            selectedQuantities = uiState.suggestedQuantities,
            onIncrement = viewModel::incrementSuggested,
            onDecrement = viewModel::decrementSuggested,
            gotoCart = {
                viewModel.confirmSuggestedSelectionToCart(onDone = { navigateToCart() })
            },
        )
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Details",
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
                    QuantityStepper(
                        quantity = quantity,
                        onMinusClick = viewModel::productQtyDecrement,
                        onPlusClick = viewModel::productQtyIncrement
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }
    ) { paddingValues ->
        productState.DisplayResult(
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onError = { message ->
                InfoCard(
                    image = Resources.Icon.Dog,
                    title = "Oops!",
                    subtitle = message
                )
            },
            onSuccess = { product ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues = paddingValues)
                        .padding(12.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(product.productImage)
                            .crossfade(enable = true)
                            .build(),
                        contentDescription = "Product image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = BorderIdle,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            ProductDetailsCard(
                                title = product.title,
                                description = product.description,
                                energyValue = product.energyValue,
                                price = product.price,
                                allergyAdvice = product.allergyAdvice,
                                ingredients = product.ingredients
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            DetailsBottomActions(
                                onFavourite = viewModel::toggleFavourite,
                                onBuyNow = viewModel::buyNow,
                                onAddToCart = viewModel::addToCart,
                                isFavourite = uiState.isFavourite
                            )
                        }
                    }
                    PrimaryButton(
                        text = "Browse for More",
                        icon = painterResource(Resources.Icon.Book),
                        enabled = true,
                        onClick = navigateToMenu
                    )
                }
            }
        )
    }
}


@Composable
private fun ProductDetailsCard(
    title: String,
    description: String,
    energyValue: Int?,
    price: Double,
    allergyAdvice: String,
    ingredients: String
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        colors = CardDefaults.cardColors(SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Resources.Icon.Flame),
                        contentDescription = "Flame icon",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${energyValue ?: 0}kcal",
                        fontSize = FontSize.REGULAR
                    )
                }
                Text(
                    text = "£${price}",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Bold,
                    color = BrandBrown
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = FontSize.REGULAR,
            )
            Spacer(modifier = Modifier.height(8.dp))
            DetailsInfoSection(
                title = "Aller Advice",
                body = allergyAdvice
            )
            Spacer(modifier = Modifier.height(8.dp))
            DetailsInfoSection(
                title = "Ingredients",
                body = ingredients,
            )
        }
    }
}

@Composable
private fun DetailsInfoSection(
    title: String,
    body: String
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(SurfaceLight)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = FontSize.REGULAR
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                fontSize = FontSize.REGULAR
            )
        }
    }
}

@Composable
private fun DetailsBottomActions(
    isFavourite: Boolean,
    onFavourite: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
       OutlinedIconButton(
           onClick = onFavourite,
           modifier = Modifier.size(46.dp),
           shape = RoundedCornerShape(12.dp),
           border = BorderStroke(1.dp, BorderIdle)
       ) {
           Icon(
               painter = painterResource(
                   if (isFavourite) Resources.Icon.HeartFilled else Resources.Icon.Heart),
               contentDescription = "Heart icon",
               modifier = Modifier.size(24.dp),
               tint = if (isFavourite) BrandBrown else IconPrimary
           )
       }
        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .height(46.dp)
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(BrandYellow)
        ) {
            Text(
                text = "Add to Cart",
                fontWeight = FontWeight.Bold,
                fontSize = FontSize.REGULAR,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(Resources.Icon.ShoppingCart),
                contentDescription = "Shopping cart icon",
                modifier = Modifier.size(16.dp),
                tint = IconPrimary
            )
        }
        Button(
            onClick = onBuyNow,
            modifier = Modifier
                .height(46.dp)
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(BrandYellow)
        ) {
            Text(
                text = "Buy Now",
                fontWeight = FontWeight.Bold,
                fontSize = FontSize.REGULAR,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(Resources.Icon.Pound),
                contentDescription = "Pound Sterling icon",
                modifier = Modifier.size(16.dp),
                tint = IconPrimary
            )
        }
    }
}