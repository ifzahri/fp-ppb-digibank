package ppb.eas.digibank.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ppb.eas.digibank.data.Transaction
import ppb.eas.digibank.viewmodel.TransactionViewModel
import ppb.eas.digibank.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryScreen(
    transactionViewModel: TransactionViewModel,
    userViewModel: UserViewModel,
    currentUserId: Int
) {
    val transactions by transactionViewModel.getTransactionsForUser(currentUserId).observeAsState(emptyList())
    val allUsers by userViewModel.allUsers.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Transaction History", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (transactions.isEmpty()) {
            Text(text = "No transactions found.", fontSize = 18.sp)
        } else {
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(transaction, allUsers)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, allUsers: List<ppb.eas.digibank.data.User>) {
    val sender = allUsers.find { it.id == transaction.senderId }
    val receiver = allUsers.find { it.id == transaction.receiverId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Amount: $${transaction.amount}", fontSize = 18.sp)
            Text(text = "From: ${sender?.username ?: "Unknown"}", fontSize = 14.sp)
            Text(text = "To: ${receiver?.username ?: "Unknown"}", fontSize = 14.sp)
            Text(text = "Date: ${formatTimestamp(transaction.timestamp)}", fontSize = 12.sp)
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
