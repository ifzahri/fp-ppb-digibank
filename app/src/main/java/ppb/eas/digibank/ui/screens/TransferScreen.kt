package ppb.eas.digibank.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ppb.eas.digibank.data.User
import ppb.eas.digibank.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    // Simplified .collectAsState() call
    val allUsers by userViewModel.allUsers.collectAsState()
    val loggedInUser by userViewModel.loggedInUser.collectAsState()
    val transferResult by userViewModel.transferResult.collectAsState()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var amount by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(TransferStep.SELECT_RECIPIENT) }

    LaunchedEffect(transferResult) {
        transferResult?.let { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(context, "Transfer Successful!", Toast.LENGTH_SHORT).show()
                    userViewModel.resetTransferResult()
                    onBack()
                },
                onFailure = { error ->
                    Toast.makeText(context, "Transfer Failed: ${error.message}", Toast.LENGTH_LONG).show()
                    userViewModel.resetTransferResult()
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Funds") },
                navigationIcon = {
                    IconButton(onClick = {
                        when (step) {
                            TransferStep.SELECT_RECIPIENT -> onBack()
                            TransferStep.ENTER_AMOUNT -> step = TransferStep.SELECT_RECIPIENT
                            TransferStep.CONFIRM_PIN -> step = TransferStep.ENTER_AMOUNT
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (step) {
                TransferStep.SELECT_RECIPIENT -> {
                    val otherUsers = allUsers.filter { it.id != loggedInUser?.id }
                    UserSelectionList(users = otherUsers) { user ->
                        selectedUser = user
                        step = TransferStep.ENTER_AMOUNT
                    }
                }
                TransferStep.ENTER_AMOUNT -> {
                    selectedUser?.let {
                        AmountEntry(recipient = it) { enteredAmount ->
                            amount = enteredAmount
                            step = TransferStep.CONFIRM_PIN
                        }
                    }
                }
                TransferStep.CONFIRM_PIN -> {
                    PinConfirmation(
                        onConfirm = { enteredPin ->
                            pin = enteredPin
                            selectedUser?.let { recipient ->
                                amount.toDoubleOrNull()?.let { transferAmount ->
                                    userViewModel.transferFunds(recipient, transferAmount, pin)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserSelectionList(users: List<User>, onUserSelected: (User) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(users) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserSelected(user) },
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(user.name, style = MaterialTheme.typography.bodyLarge)
                    Text(user.accountNumber, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun AmountEntry(recipient: User, onAmountEntered: (String) -> Unit) {
    var amount by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Transfer to: ${recipient.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { char -> char.isDigit() } },
            label = { Text("Amount") },
            leadingIcon = { Text("Rp") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onAmountEntered(amount) }, enabled = amount.isNotBlank() && amount.toDoubleOrNull() ?: 0.0 > 0) {
            Text("Next")
        }
    }
}

@Composable
private fun PinConfirmation(onConfirm: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter Your PIN", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 6) pin = it.filter { c -> c.isDigit() } },
            label = { Text("6-digit PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onConfirm(pin) }, enabled = pin.length > 0) {
            Text("Confirm Transfer")
        }
    }
}


private enum class TransferStep {
    SELECT_RECIPIENT,
    ENTER_AMOUNT,
    CONFIRM_PIN
}