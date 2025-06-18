package ppb.eas.digibank.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ppb.eas.digibank.viewmodel.TransactionViewModel
import ppb.eas.digibank.viewmodel.UserViewModel

@Composable
fun TransferScreen(
    userViewModel: UserViewModel,
    transactionViewModel: TransactionViewModel,
    currentUserId: Int,
    onTransferSuccess: () -> Unit
) {
    val currentUser by userViewModel.getUserById(currentUserId).observeAsState()
    var receiverUsername by remember { mutableStateOf("") }
    var amountString by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Transfer Money", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = receiverUsername,
            onValueChange = { receiverUsername = it },
            label = { Text("Receiver Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amountString,
            onValueChange = { newValue ->
                amountString = newValue.filter { it.isDigit() || it == '.' }
            },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amount = amountString.toDoubleOrNull()
                if (currentUser != null && amount != null && amount > 0) {
                    if (currentUser!!.balance >= amount) {
                        transactionViewModel.viewModelScope.launch {
                            val success = transactionViewModel.transferMoney(
                                currentUser!!,
                                receiverUsername,
                                amount
                            )
                            if (success) {
                                message = "Transfer successful!"
                                onTransferSuccess()
                            } else {
                                message = "Transfer failed. Receiver not found or other error."
                            }
                        }
                    } else {
                        message = "Insufficient funds."
                    }
                } else {
                    message = "Please enter valid receiver and amount."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Transfer")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, color = if (message.contains("successful")) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red)
    }
}
