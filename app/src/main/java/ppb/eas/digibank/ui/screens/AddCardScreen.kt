package ppb.eas.digibank.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.ui.components.BackButton
import ppb.eas.digibank.viewmodel.CardViewModel
import ppb.eas.digibank.viewmodel.CardViewModelFactory
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    navController: NavHostController,
    userId: Int,
    cardViewModel: CardViewModel = viewModel(factory = CardViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var isConfirmPinVisible by remember { mutableStateOf(false) }
    var isPinError by remember { mutableStateOf(false) }
    var pinErrorMessage by remember { mutableStateOf("") }
    var isGeneralError by remember { mutableStateOf(false) }
    var generalErrorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add New Card",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    BackButton(navController = navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Create Your New Card",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Fill in the details below to add a new card to your account",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                    )
                }
            }

            // Form Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card Information Section
                    SectionHeader(
                        title = "Card Information",
                        icon = Icons.Default.CreditCard
                    )

                    CustomTextField(
                        value = cardHolderName,
                        onValueChange = { cardHolderName = it },
                        label = "Card Holder Name",
                        leadingIcon = Icons.Default.Person
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = expiryDate,
                            onValueChange = {
                                // Format expiry date as MM/YY
                                val cleaned = it.replace("/", "")
                                val formatted = when {
                                    cleaned.length >= 2 -> "${cleaned.substring(0, 2)}/${cleaned.substring(2, minOf(4, cleaned.length))}"
                                    else -> cleaned
                                }
                                if (formatted.length <= 5) expiryDate = formatted
                            },
                            label = "MM/YY",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number
                        )

                        CustomTextField(
                            value = cvv,
                            onValueChange = { if (it.length <= 3) cvv = it },
                            label = "CVV",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = cardType,
                            onValueChange = { cardType = it },
                            label = "Card Type",
                            modifier = Modifier.weight(1f),
                            placeholder = "Visa, Mastercard"
                        )

                        CustomTextField(
                            value = balance,
                            onValueChange = { balance = it },
                            label = "Initial Balance",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number,
                            prefix = "Rp"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Security Section
                    SectionHeader(
                        title = "Security",
                        icon = Icons.Default.Lock
                    )

                    CustomTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 6) pin = it },
                        label = "6-Digit PIN",
                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardType = KeyboardType.NumberPassword,
                        isError = isPinError,
                        trailingIcon = {
                            IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                Icon(
                                    imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPinVisible) "Hide PIN" else "Show PIN",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    )

                    CustomTextField(
                        value = confirmPin,
                        onValueChange = { if (it.length <= 6) confirmPin = it },
                        label = "Confirm PIN",
                        visualTransformation = if (isConfirmPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardType = KeyboardType.NumberPassword,
                        isError = isPinError,
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPinVisible = !isConfirmPinVisible }) {
                                Icon(
                                    imageVector = if (isConfirmPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isConfirmPinVisible) "Hide PIN" else "Show PIN",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    )

                    // Error Messages
                    if (isPinError) {
                        ErrorMessage(pinErrorMessage)
                    }

                    if (isGeneralError) {
                        ErrorMessage(generalErrorMessage)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add Card Button
                    Button(
                        onClick = {
                            Log.d("AddCardScreen", "Add Card button clicked")
                            Log.d("AddCardScreen", "UserId: $userId")

                            // Reset errors
                            isPinError = false
                            isGeneralError = false

                            // Validate user ID
                            if (userId == -1) {
                                Log.e("AddCardScreen", "Invalid userId: $userId")
                                isGeneralError = true
                                generalErrorMessage = "User session expired. Please login again."
                                return@Button
                            }

                            // Validate all fields
                            if (cardHolderName.isBlank()) {
                                isGeneralError = true
                                generalErrorMessage = "Please enter card holder name."
                                return@Button
                            }

                            if (expiryDate.isBlank()) {
                                isGeneralError = true
                                generalErrorMessage = "Please enter expiry date."
                                return@Button
                            }

                            if (cvv.isBlank()) {
                                isGeneralError = true
                                generalErrorMessage = "Please enter CVV."
                                return@Button
                            }

                            if (cardType.isBlank()) {
                                isGeneralError = true
                                generalErrorMessage = "Please enter card type."
                                return@Button
                            }

                            if (balance.isBlank() || balance.toDoubleOrNull() == null) {
                                isGeneralError = true
                                generalErrorMessage = "Please enter valid initial balance."
                                return@Button
                            }

                            // Validate PIN
                            val isPinValid = pin.length == 6
                            val arePinsMatching = pin == confirmPin

                            if (!isPinValid) {
                                isPinError = true
                                pinErrorMessage = "PIN must be 6 digits."
                                return@Button
                            }

                            if (!arePinsMatching) {
                                isPinError = true
                                pinErrorMessage = "PINs do not match."
                                return@Button
                            }

                            // All validations passed, create card
                            try {
                                val cardNumber = (1..16).map { Random.nextInt(0, 10) }.joinToString("")
                                val newCard = Card(
                                    id_user = userId,
                                    cardHolderName = cardHolderName,
                                    cardNumber = cardNumber,
                                    expiryDate = expiryDate,
                                    cvv = cvv,
                                    cardType = cardType,
                                    balance = balance.toDouble(),
                                    pin = pin
                                )

                                Log.d("AddCardScreen", "Creating new card: $newCard")
                                cardViewModel.insert(newCard)
                                Log.d("AddCardScreen", "Card inserted successfully, navigating back")
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Log.e("AddCardScreen", "Error creating card: ${e.message}", e)
                                isGeneralError = true
                                generalErrorMessage = "Error creating card. Please try again."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Add Card",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    placeholder: String? = null,
    prefix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        placeholder = placeholder?.let { { Text(it, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) } },
        prefix = prefix?.let { { Text(it, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) } },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
}