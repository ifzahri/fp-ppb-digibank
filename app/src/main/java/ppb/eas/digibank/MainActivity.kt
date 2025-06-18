package ppb.eas.digibank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ppb.eas.digibank.ui.screens.AddCardScreen
import ppb.eas.digibank.ui.screens.HomeScreen
import ppb.eas.digibank.ui.screens.ManageCardsScreen
import ppb.eas.digibank.ui.screens.PinScreen
import ppb.eas.digibank.ui.screens.TopUpScreen
import ppb.eas.digibank.ui.screens.TransactionHistoryScreen
import ppb.eas.digibank.ui.screens.TransferScreen
import ppb.eas.digibank.ui.theme.DigiBankTheme
import ppb.eas.digibank.viewmodel.CardViewModelFactory
import ppb.eas.digibank.viewmodel.TransactionViewModel
import ppb.eas.digibank.viewmodel.TransactionViewModelFactory
import ppb.eas.digibank.viewmodel.UserViewModel
import ppb.eas.digibank.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(application)
    }

    // In a real app, this userId (1) would be retrieved after a user logs in.
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(application, 1)
    }

    private val cardViewModelFactory by lazy { CardViewModelFactory(application, 1) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigiBankTheme {
                val navController = rememberNavController()
                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                userViewModel = userViewModel,
                                onNavigateToTransfer = { navController.navigate("transfer") },
                                onNavigateToHistory = { navController.navigate("history") },
                                onNavigateToTopUp = { navController.navigate("topup") },
                                onNavigateToCards = { navController.navigate("cards") },
                                onNavigateToSetPin = { navController.navigate("set_pin") }
                            )
                        }
                        composable("transfer") {
                            TransferScreen(
                                userViewModel = userViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("history") {
                            TransactionHistoryScreen(
                                transactionViewModel = transactionViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("topup") {
                            TopUpScreen(
                                userViewModel = userViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("cards") {
                            ManageCardsScreen(
                                cardViewModelFactory = cardViewModelFactory,
                                onBack = { navController.popBackStack() },
                                onNavigateToAddCard = { navController.navigate("add_card") }
                            )
                        }
                        composable("add_card") {
                            AddCardScreen(
                                cardViewModelFactory = cardViewModelFactory,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("set_pin") {
                            PinScreen(
                                userViewModel = userViewModel,
                                onPinSet = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}