package ppb.eas.digibank.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ppb.eas.digibank.viewmodel.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    currentUserId: Int,
    onNavigateToTransfer: () -> Unit,
    onNavigateToTransactionHistory: () -> Unit
) {
    val currentUser by userViewModel.getUserById(currentUserId).observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome, ${currentUser?.username ?: "User"}", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Your Balance:", fontSize = 20.sp)
        Text(text = "$${currentUser?.balance ?: 0.0}", fontSize = 36.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateToTransfer) {
            Text(text = "Transfer Money")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToTransactionHistory) {
            Text(text = "View Transaction History")
        }
    }
}
