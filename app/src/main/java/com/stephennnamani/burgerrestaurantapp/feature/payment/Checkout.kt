package com.stephennnamani.burgerrestaurantapp.feature.payment

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephennnamani.burgerrestaurantapp.feature.component.InfoCard
import com.stephennnamani.burgerrestaurantapp.feature.payment.paypal.CheckoutViewModel
import com.stephennnamani.burgerrestaurantapp.feature.payment.paypal.PayPalCheckoutViewModel
import com.stephennnamani.burgerrestaurantapp.feature.payment.paypal.PayPalPaymentResult
import com.stephennnamani.burgerrestaurantapp.feature.util.Alpha
import com.stephennnamani.burgerrestaurantapp.feature.util.DisplayResult
import com.stephennnamani.burgerrestaurantapp.feature.util.MessageUtils
import com.stephennnamani.burgerrestaurantapp.feature.util.RequestState
import com.stephennnamani.burgerrestaurantapp.ui.theme.BorderIdle
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandBrown
import com.stephennnamani.burgerrestaurantapp.ui.theme.BrandYellow
import com.stephennnamani.burgerrestaurantapp.ui.theme.FontSize
import com.stephennnamani.burgerrestaurantapp.ui.theme.IconPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.Resources
import com.stephennnamani.burgerrestaurantapp.ui.theme.Surface
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceBrand
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceDark
import com.stephennnamani.burgerrestaurantapp.ui.theme.SurfaceLight
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextPrimary
import com.stephennnamani.burgerrestaurantapp.ui.theme.TextWhite
import com.stephennnamani.burgerrestaurantapp.ui.theme.oswaldVariableFont
import org.koin.androidx.compose.koinViewModel

enum class PaymentMethod{
    Card,
    Paypal
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navigateBack: () -> Unit,
    navigateCart: () -> Unit,
    totalAmount: Double
){
    val checkoutVm = koinViewModel<CheckoutViewModel>()
    val payPalVm = koinViewModel<PayPalCheckoutViewModel>()

    val checkoutUiState by checkoutVm.uiState.collectAsStateWithLifecycle()
    val payPalUiState by payPalVm.payPalUiState.collectAsStateWithLifecycle()

    var method by remember { mutableStateOf(PaymentMethod.Card) }
    var savedCard by remember { mutableStateOf(true) }

    val activity = LocalContext.current

    MessageUtils.ShowToast(message = payPalUiState.toast)

    LaunchedEffect(payPalUiState.toast) {
        if (payPalUiState.toast.isNotBlank()) payPalVm.consumeToast()
    }

    LaunchedEffect(payPalUiState.navigateToCart) {
        if (payPalUiState.navigateToCart){
            payPalVm.consumeNavigateToCart()
            navigateCart()
        }
    }


    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout",
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
                    Text(
                        text = "£${"%.2f".format(totalAmount)}",
                        fontFamily = oswaldVariableFont(),
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.Bold,
                        color = BrandBrown,
                        modifier = Modifier.padding(end = 12.dp)
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PaymentMethodToggle(
                selected = method,
                onSelect = { method = it }
            )

            Card(
                modifier = Modifier.fillMaxWidth()
                    .dropShadow(
                        shape = RoundedCornerShape(12.dp),
                        shadow = Shadow(
                            radius = 4.dp,
                            spread = 1.dp,
                            color = BorderIdle,
                            offset = DpOffset(x = 4.dp, y = 4.dp)
                        )
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (method == PaymentMethod.Card){
                        CardPaymentForm(
                            savedCard = savedCard,
                            onToggleSave = { savedCard = it }
                        )
                    } else {
                        PayPalSection(payPalUiState.state)
                    }
                }
            }

            checkoutUiState.delivery.DisplayResult(
                onLoading = {
                    DeliveryDetailsCard(
                        address = "Loading...",
                        postCode = "Loading...",
                        onEditAddress = {},
                        onEditPostcode = {}
                    )
                },
                onError = {
                    DeliveryDetailsCard(
                        address = "Unknown",
                        postCode = "Unknown",
                        onEditAddress = {},
                        onEditPostcode = {}
                    )
                },
                onSuccess = { delivery ->
                    DeliveryDetailsCard(
                        address = delivery.addressLine,
                        postCode = delivery.postcode,
                        onEditAddress = {},
                        onEditPostcode = {}
                    )
                }
            )

            Button(
                onClick = {
                    if (method == PaymentMethod.Card){
                        //Stub
                        return@Button
                    }
                    payPalVm.startPayPalCheckout(
                        activity as ComponentActivity,
                        totalAmount
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(BrandYellow)
            ) {
                Text(
                    text = "CONFIRM & PAY",
                    fontFamily = oswaldVariableFont(),
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun PayPalSection(state: RequestState<PayPalPaymentResult>){
    state.DisplayResult(
        onIdle = {
            InfoCard(
                image = Resources.Image.PaypalLogo,
                title = "PayPal ready",
                subtitle = "Tap CONFIRM & PAY to continue."
            )
        },
        onLoading = {
            InfoCard(
                image = Resources.Image.PaypalLogo,
                title = "Processing...",
                subtitle = "Hang tight - we're talking to PayPal securely."
            )
        },
        onError = { msg ->
            InfoCard(
                image = Resources.Icon.Dog,
                title = "Oops!",
                subtitle = msg
            )
        },
        onSuccess = { result ->
            InfoCard(
                image = Resources.Image.PaypalLogo,
                title = "Payment ${result.status}",
                subtitle = "Capture: ${result.captureId ?: "-"}"
            )
        }
    )
}


@Composable
private fun PaymentMethodToggle(
    selected: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TogglePill(
            text = "Card",
            selected = selected == PaymentMethod.Card,
            onClick = { onSelect(PaymentMethod.Card)},
            leadingIcon = Resources.Icon.Card
        )
        Spacer(modifier = Modifier.width(8.dp))
        TogglePill(
            text = "Paypal",
            selected = selected == PaymentMethod.Paypal,
            onClick = { onSelect(PaymentMethod.Paypal)},
            leadingIcon = Resources.Image.PaypalLogo
        )
    }
}

@Composable
private fun TogglePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: Int
){
    val background = if (selected) BrandBrown else SurfaceLight
    val  foreground = if (selected) TextWhite else TextPrimary

    Button(
        onClick = onClick,
        modifier = Modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(background),
        border = BorderStroke(1.dp, BrandBrown)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = foreground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(leadingIcon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun CardPaymentForm(
    savedCard: Boolean,
    onToggleSave: (Boolean) -> Unit
){
    Text(
        text = "CARD NUMBER",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = "1234 5678 9012 3456",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        trailingIcon = {
            Icon(
                painter = painterResource(Resources.Icon.MasterCard),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = TextPrimary
        )
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "NAME ON CARD",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = "Stephen Nnamani",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = TextPrimary
        )
    )

    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "EXPIRY DATE",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        DropdownStub(value = "01", modifier = Modifier.weight(1f))
        DropdownStub(value = "26", modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "CVV",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CvvBox(value = "6")
        CvvBox(value = "1")
        CvvBox(value = "2")
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(
       modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Save card details",
            fontSize = FontSize.REGULAR,
            color = TextPrimary
        )
        Switch(
            checked = savedCard,
            onCheckedChange = onToggleSave,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SurfaceBrand,
                uncheckedTrackColor = SurfaceDark,
                checkedThumbColor = Surface,
                uncheckedThumbColor = Surface,
                checkedBorderColor = SurfaceBrand,
                uncheckedBorderColor = SurfaceDark
            )
        )
    }
}

@Composable
private fun DropdownStub(
    value: String,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = value,
        onValueChange = {},
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.width(120.dp),
        singleLine = true,
        trailingIcon = {
            Icon(
                painter = painterResource(Resources.Icon.Dropdown),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandYellow,
            unfocusedTextColor = TextPrimary

        )
    )
}

@Composable
private fun CvvBox(value: String){
    OutlinedTextField(
        value = value,
        onValueChange = {},
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(54.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandYellow,
            unfocusedTextColor = TextPrimary
        )
    )
}

@Composable
private fun DeliveryDetailsCard(
    address: String,
    postCode: String?,
    onEditAddress: () -> Unit,
    onEditPostcode: () -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Surface)
    ) {
        Column(
            modifier = Modifier
                .border(
                    1.dp,
                    BrandBrown,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Delivery details:",
                fontSize = FontSize.REGULAR,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            DeliveryRow(value = address, onEdit = onEditAddress)
            Spacer(modifier = Modifier.height(8.dp))
            DeliveryRow(value = postCode ?: "Unknown", onEdit = onEditPostcode)
        }
    }
}

@Composable
private fun DeliveryRow(
    value: String,
    onEdit: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(SurfaceLight)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value,
            fontSize = FontSize.REGULAR,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedButton(
            onClick = onEdit,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.width(100.dp),
            colors = ButtonDefaults.outlinedButtonColors(Surface)
        ){
            Text(
                text = "Edit",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
        }
    }
}

//@Composable
//private fun PayPalPlaceHolder(){
//    Column(modifier = Modifier.height(300.dp)) {
//        InfoCard(
//            image = Resources.Icon.Dog,
//            title = "Oops!",
//            subtitle = "Paypal checkout coming next."
//        )
//    }
//}