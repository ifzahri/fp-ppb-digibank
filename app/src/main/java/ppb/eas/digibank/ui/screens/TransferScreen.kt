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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.data.Payee
import ppb.eas.digibank.ui.components.BackButton
import ppb.eas.digibank.viewmodel.CardViewModel
import ppb.eas.digibank.viewmodel.CardViewModelFactory
import ppb.eas.digibank.viewmodel.PayeeViewModel
import ppb.eas.digibank.viewmodel.PayeeViewModelFactory
import ppb.eas.digibank.viewmodel.TransactionViewModel
import ppb.eas.digibank.viewmodel.TransactionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    navController: NavHostController,
    cardViewModel: CardViewModel = viewModel(factory = CardViewModelFactory(LocalContext.current.applicationContext as Application)),
    transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(LocalContext.current.applicationContext as Application)),
    payeeViewModel: PayeeViewModel = viewModel(factory = PayeeViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val context = LocalContext.current
    val cards by cardViewModel.cards.observeAsState(initial = emptyList())
    val payees by payeeViewModel.payees.collectAsState(initial = emptyList())

    var fromCard by remember { mutableStateOf<Card?>(null) }
    var selectedPayee by remember { mutableStateOf<Payee?>(null) }
    var toBank by remember { mutableStateOf("") }
    var toAccNumber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var fromCardExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer to Other Bank") },
                navigationIcon = {
                    BackButton(navController = navController)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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

            // Payee selection
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = payeeExpanded,
                    onExpandedChange = { payeeExpanded = !payeeExpanded }
                ) {
                    TextField(
                        value = selectedPayee?.name ?: "Select Payee or Enter Manually",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("To Payee") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = payeeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = payeeExpanded,
                        onDismissRequest = { payeeExpanded = false }
                    ) {
                        payees.forEach { payee ->
                            DropdownMenuItem(
                                text = { Text("${payee.name} - ${payee.accountNumber}") },
                                onClick = {
                                    selectedPayee = payee
                                    toBank = payee.bankName
                                    toAccNumber = payee.accountNumber
                                    payeeExpanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Enter manually...") },
                            onClick = {
                                selectedPayee = null
                                toBank = ""
                                toAccNumber = ""
                                payeeExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = toBank,
                onValueChange = { toBank = it },
                label = { Text("Bank Tujuan") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = selectedPayee != null
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = toAccNumber,
                onValueChange = { toAccNumber = it },
                label = { Text("Nomor Rekening Tujuan") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                readOnly = selectedPayee != null
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Nominal") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val from = fromCard
                    val transferAmount = amount.toDoubleOrNull()

                    if (from == null || toBank.isBlank() || toAccNumber.isBlank() || transferAmount == null || transferAmount <= 0) {
                        Toast.makeText(context, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    transactionViewModel.transfer(
                        fromCardId = from.id,
                        toBank = toBank,
                        toAccNumber = toAccNumber,
                        amount = transferAmount,
                        onSuccess = {
                            Toast.makeText(context, "Transfer successful!", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        },
                        onError = {errorMsg ->
                            Toast.makeText(context, "Transfer failed: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Transfer")
            }
        }
    }
}
