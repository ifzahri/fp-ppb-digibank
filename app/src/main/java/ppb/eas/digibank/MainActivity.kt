package ppb.eas.digibank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.UserRepository
import ppb.eas.digibank.ui.screens.HomeScreen
import ppb.eas.digibank.ui.screens.TransferScreen
import ppb.eas.digibank.ui.screens.TransactionHistoryScreen
import ppb.eas.digibank.ui.theme.DigibankTheme
import ppb.eas.digibank.viewmodel.UserViewModel
import ppb.eas.digibank.viewmodel.UserViewModelFactory
import ppb.eas.digibank.data.TransactionRepository
import ppb.eas.digibank.viewmodel.TransactionViewModel
import ppb.eas.digibank.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(database.userDao())
        val transactionRepository = TransactionRepository(database.transactionDao())

        val userFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, userFactory).get(UserViewModel::class.java)

        val transactionFactory = TransactionViewModelFactory(transactionRepository, userRepository)
        transactionViewModel = ViewModelProvider(this, transactionFactory).get(TransactionViewModel::class.java)

        setContent {
            DigibankTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // You can now use userViewModel to interact with your data
                    // For example, to observe all users:
                    val navController = rememberNavController()
                    val currentUserId = 1 // This should be dynamically set after user login/registration, e.g., from a login screen

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                userViewModel = userViewModel,
                                currentUserId = currentUserId,
                                onNavigateToTransfer = { navController.navigate("transfer") },
                                onNavigateToTransactionHistory = { navController.navigate("transactionHistory") }
                            )
                        }
                        composable("transfer") {
                            TransferScreen(
                                userViewModel = userViewModel,
                                transactionViewModel = transactionViewModel,
                                currentUserId = currentUserId,
                                onTransferSuccess = { navController.popBackStack() }
                            )
                        }
                        composable("transactionHistory") {
                            TransactionHistoryScreen(
                                transactionViewModel = transactionViewModel,
                                userViewModel = userViewModel,
                                currentUserId = currentUserId
                            )
                        }
                    }
                }
            }
        }
    }
}
