package ppb.eas.digibank.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.viewmodel.CardViewModel
import ppb.eas.digibank.viewmodel.CardViewModelFactory
import kotlin.random.Random

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
    var isPinError by remember { mutableStateOf(false) }
    var pinErrorMessage by remember { mutableStateOf("") }
    var isGeneralError by remember { mutableStateOf(false) }
    var generalErrorMessage by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Add a New Card", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cardHolderName,
                onValueChange = { cardHolderName = it },
                label = { Text("Card Holder Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = it },
                label = { Text("Expiry Date (MM/YY)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it },
                label = { Text("CVV") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cardType,
                onValueChange = { cardType = it },
                label = { Text("Card Type (e.g., Visa, Mastercard)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Initial Balance") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6) pin = it },
                label = { Text("6-Digit PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                isError = isPinError,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPin,
                onValueChange = { if (it.length <= 6) confirmPin = it },
                label = { Text("Confirm PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                isError = isPinError,
                modifier = Modifier.fillMaxWidth()
            )

            if (isPinError) {
                Text(
                    text = pinErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            if (isGeneralError) {
                Text(
                    text = generalErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Card")
            }
        }
    }
}