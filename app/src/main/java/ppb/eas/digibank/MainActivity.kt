package ppb.eas.digibank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ppb.eas.digibank.ui.screens.AddCardScreen
import ppb.eas.digibank.ui.screens.HomeScreen
import ppb.eas.digibank.ui.screens.InternalTransferScreen
import ppb.eas.digibank.ui.screens.ManageCardsScreen
import ppb.eas.digibank.ui.screens.ManagePayeesScreen
import ppb.eas.digibank.ui.screens.PinScreen
import ppb.eas.digibank.ui.screens.TopUpScreen
import ppb.eas.digibank.ui.screens.TransactionHistoryScreen
import ppb.eas.digibank.ui.screens.TransferScreen
import ppb.eas.digibank.ui.theme.DigiBankTheme
import ppb.eas.digibank.viewmodel.UserViewModel
import ppb.eas.digibank.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(application))[UserViewModel::class.java]

        setContent {
            DigiBankTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val currentUser by userViewModel.currentUser.observeAsState()

                    val startDestination = if (currentUser == null) "login" else "home"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            PinScreen(
                                userViewModel = userViewModel,
                                onPinEntered = {
                                    navController.navigate("home"){
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                userId = currentUser?.id ?: run {
                                    // If currentUser is null, navigate back to login
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                    return@run -1
                                }
                            )
                        }
                        composable("addCard") {
                            val userId = currentUser?.id ?: run {
                                // If currentUser is null, navigate back to login
                                navController.navigate("login") {
                                    popUpTo("addCard") { inclusive = true }
                                }
                                return@run -1
                            }
                            if (userId != -1) {
                                AddCardScreen(navController = navController, userId = userId)
                            }
                        }
                        composable("topUp") { TopUpScreen(navController = navController) }
                        composable("transfer") { TransferScreen(navController = navController) }
                        composable("transactionHistory") {
                            val userId = currentUser?.id ?: run {
                                navController.navigate("login") {
                                    popUpTo("transactionHistory") { inclusive = true }
                                }
                                return@run -1
                            }
                            if (userId != -1) {
                                TransactionHistoryScreen(navController = navController, userId = userId)
                            }
                        }
                        composable("internalTransfer") { InternalTransferScreen(navController = navController) }
                        composable("managePayees") {
                            val userId = currentUser?.id ?: run {
                                navController.navigate("login") {
                                    popUpTo("managePayees") { inclusive = true }
                                }
                                return@run -1
                            }
                            if (userId != -1) {
                                ManagePayeesScreen(navController = navController, userId = userId)
                            }
                        }
                        composable("manageCards") {
                            val userId = currentUser?.id ?: run {
                                navController.navigate("login") {
                                    popUpTo("manageCards") { inclusive = true }
                                }
                                return@run -1
                            }
                            if (userId != -1) {
                                ManageCardsScreen(navController = navController, userId = userId)
                            }
                        }
                    }
                }
            }
        }
    }
}