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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.viewmodel.CardViewModel
import ppb.eas.digibank.viewmodel.CardViewModelFactory

@Composable
fun HomeScreen(
    navController: NavHostController,
    userId: Int,
    cardViewModel: CardViewModel = viewModel(factory = CardViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val cards by cardViewModel.cards.observeAsState(initial = emptyList())
    val userCards = cards.filter { it.id_user == userId }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Welcome!", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            ActionButtons(navController)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Your Cards", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            if (userCards.isEmpty()) {
                Text("No cards found. Add one to get started.", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(userCards) { card ->
                        CardItem(card)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(navController: NavHostController) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { navController.navigate("addCard") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = "Add Card", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Add New Card")
        }
        Button(onClick = { navController.navigate("internalTransfer") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Send, contentDescription = "Internal Transfer", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Internal Transfer")
        }
        Button(onClick = { navController.navigate("transfer") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Send, contentDescription = "Transfer", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Transfer to Other Bank")
        }
         Button(onClick = { navController.navigate("manageCards") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.CreditCard, contentDescription = "Manage Cards", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Manage Cards")
        }
        Button(onClick = { navController.navigate("managePayees") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = "Manage Payees", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Manage Payees")
        }
        Button(onClick = { navController.navigate("topUp") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = "Top Up", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Top Up")
        }
        Button(onClick = { navController.navigate("transactionHistory") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = "History", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Transaction History")
        }
    }
}


@Composable
private fun CardItem(card: Card) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(card.cardType, style = MaterialTheme.typography.titleMedium)
            Text(card.cardHolderName)
            Text(".... .... .... ${card.cardNumber.takeLast(4)}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Balance: Rp. ${"%,.2f".format(card.balance)}")
                Text("Exp: ${card.expiryDate}")
            }
        }
    }
}
