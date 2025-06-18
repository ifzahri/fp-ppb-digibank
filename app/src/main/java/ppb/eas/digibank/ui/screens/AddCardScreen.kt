package ppb.eas.digibank.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.viewmodel.CardViewModel
import ppb.eas.digibank.viewmodel.CardViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    cardViewModelFactory: CardViewModelFactory,
    onBack: () -> Unit
) {
    val cardViewModel: CardViewModel = viewModel(factory = cardViewModelFactory)

    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Card") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Card Holder Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = provider, onValueChange = { provider = it }, label = { Text("Provider (e.g., Visa)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = number, onValueChange = { if(it.length <= 16) number = it.filter { c -> c.isDigit() }}, label = { Text("Card Number (16 digits)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(value = expiry, onValueChange = { if(it.length <= 5) expiry = it }, label = { Text("MM/YY") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(value = cvv, onValueChange = { if(it.length <= 3) cvv = it.filter { c -> c.isDigit() } }, label = { Text("CVV (3 digits)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val newCard = Card(
                    userId = 1, // This should be dynamic in a real app
                    cardHolderName = name,
                    cardNumber = number,
                    expiryDate = expiry,
                    cvv = cvv,
                    provider = provider
                )
                cardViewModel.addCard(newCard)
                onBack()
            }, modifier = Modifier.fillMaxWidth(), enabled = name.isNotBlank() && number.length == 16 && expiry.isNotBlank() && cvv.length == 3 && provider.isNotBlank()) {
                Text("Save Card")
            }
        }
    }
}