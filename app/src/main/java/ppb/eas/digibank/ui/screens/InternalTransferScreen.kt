package ppb.eas.digibank.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import ppb.eas.digibank.viewmodel.TransactionViewModel
import ppb.eas.digibank.viewmodel.TransactionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternalTransferScreen(
    navController: NavHostController,
    cardViewModel: CardViewModel = viewModel(factory = CardViewModelFactory(LocalContext.current.applicationContext as Application)),
    transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val context = LocalContext.current
    val cards by cardViewModel.cards.observeAsState(initial = emptyList())
    var fromCard by remember { mutableStateOf<Card?>(null) }
    var toCard by remember { mutableStateOf<Card?>(null) }
    var amount by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var fromCardExpanded by remember { mutableStateOf(false) }
    var toCardExpanded by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Internal Transfer", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // From Card Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = fromCardExpanded,
                    onExpandedChange = { fromCardExpanded = !fromCardExpanded }
                ) {
                    TextField(
                        value = fromCard?.let { ".... ${it.cardNumber.takeLast(4)} | Balance: ${"%,.2f".format(it.balance)}" } ?: "Select Source Card",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("From Card") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromCardExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = fromCardExpanded,
                        onDismissRequest = { fromCardExpanded = false }
                    ) {
                        cards.forEach { card ->
                            DropdownMenuItem(
                                text = { Text(".... ${card.cardNumber.takeLast(4)} | Balance: ${"%,.2f".format(card.balance)}") },
                                onClick = {
                                    fromCard = card
                                    fromCardExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // To Card Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = toCardExpanded,
                    onExpandedChange = { toCardExpanded = !toCardExpanded }
                ) {
                    TextField(
                        value = toCard?.let { ".... ${it.cardNumber.takeLast(4)}" } ?: "Select Destination Card",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("To Card") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toCardExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = toCardExpanded,
                        onDismissRequest = { toCardExpanded = false }
                    ) {
                        cards.filter { it.id != fromCard?.id }.forEach { card ->
                            DropdownMenuItem(
                                text = { Text(".... ${card.cardNumber.takeLast(4)}") },
                                onClick = {
                                    toCard = card
                                    toCardExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6) pin = it },
                label = { Text("Source Card PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val from = fromCard
                    val to = toCard
                    val transferAmount = amount.toDoubleOrNull()

                    if (from == null || to == null || transferAmount == null || transferAmount <= 0 || pin.isBlank()) {
                        Toast.makeText(context, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if(from.id == to.id) {
                        Toast.makeText(context, "Cannot transfer to the same card.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    transactionViewModel.transferBetweenCards(
                        fromCardId = from.id,
                        toCardId = to.id,
                        amount = transferAmount,
                        pin = pin,
                        onSuccess = {
                            Toast.makeText(context, "Transfer successful!", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, "Transfer failed: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = fromCard != null && toCard != null && amount.isNotBlank() && pin.isNotBlank()
            ) {
                Text("Transfer")
            }
        }
    }
}
