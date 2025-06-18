package ppb.eas.digibank.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ppb.eas.digibank.viewmodel.UserViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    onNavigateToTransfer: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTopUp: () -> Unit,
    onNavigateToCards: () -> Unit,
    onNavigateToSetPin: () -> Unit
) {
    val user by userViewModel.loggedInUser.collectAsState(initial = null)
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        user?.let { currentUser ->
            Text(text = "Welcome, ${currentUser.name}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Your Balance", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = numberFormat.format(currentUser.balance),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Account: ${currentUser.accountNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (currentUser.pin == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateToSetPin) {
                    Icon(Icons.Default.Lock, contentDescription = "Set PIN")
                    Text("Set Your Security PIN")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionButton(
                    icon = Icons.Default.NorthEast,
                    text = "Transfer",
                    onClick = onNavigateToTransfer
                )
                ActionButton(
                    icon = Icons.Default.SouthWest,
                    text = "Top Up",
                    onClick = onNavigateToTopUp
                )
                ActionButton(
                    icon = Icons.Default.History,
                    text = "History",
                    onClick = onNavigateToHistory
                )
                ActionButton(
                    icon = Icons.Default.CreditCard,
                    text = "Cards",
                    onClick = onNavigateToCards
                )
            }
        } ?: run {
            Text("Loading user data...")
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick, modifier = Modifier.size(64.dp)) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(32.dp))
        }
        Text(text = text, fontSize = 12.sp)
    }
}