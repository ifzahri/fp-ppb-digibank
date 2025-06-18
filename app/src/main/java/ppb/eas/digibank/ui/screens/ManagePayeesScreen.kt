package ppb.eas.digibank.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ppb.eas.digibank.data.Payee
import ppb.eas.digibank.viewmodel.PayeeViewModel
import ppb.eas.digibank.viewmodel.PayeeViewModelFactory

@Composable
fun ManagePayeesScreen(
    navController: NavHostController,
    userId: Int,
    payeeViewModel: PayeeViewModel = viewModel(factory = PayeeViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val payees by payeeViewModel.payees.collectAsState(initial = emptyList())
    var showAddPayeeDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddPayeeDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Payee")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Manage Payees", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (payees.isEmpty()) {
                Text("No payees saved. Click the '+' button to add one.", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(payees) { payee ->
                        PayeeItem(payee = payee, onDelete = { payeeViewModel.delete(it) })
                    }
                }
            }
        }
    }

    if (showAddPayeeDialog) {
        AddPayeeDialog(
            userId = userId,
            onDismiss = { showAddPayeeDialog = false },
            onConfirm = { payee ->
                payeeViewModel.insert(payee)
                showAddPayeeDialog = false
            }
        )
    }
}

@Composable
private fun PayeeItem(payee: Payee, onDelete: (Payee) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(payee.name, style = MaterialTheme.typography.titleMedium)
                Text("${payee.bankName} - ${payee.accountNumber}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onDelete(payee) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Payee")
            }
        }
    }
}

@Composable
private fun AddPayeeDialog(
    userId: Int,
    onDismiss: () -> Unit,
    onConfirm: (Payee) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Payee") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Payee Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Account Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (userId != -1) {
                        val newPayee = Payee(
                            id_user = userId,
                            name = name,
                            bankName = bankName,
                            accountNumber = accountNumber
                        )
                        onConfirm(newPayee)
                    }
                },
                enabled = name.isNotBlank() && bankName.isNotBlank() && accountNumber.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
