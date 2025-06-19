package ppb.eas.digibank.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ppb.eas.digibank.viewmodel.UserViewModel

@Composable
fun PinScreen(
    userViewModel: UserViewModel,
    onPinEntered: () -> Unit
) {
    var username by remember { mutableStateOf("ifzahri") } // Default for easy testing
    var pin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginAttempted by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Observe the current user state
    val currentUser by userViewModel.currentUser.observeAsState()

    // Handle login result
    LaunchedEffect(currentUser, loginAttempted, isLoading) {
        if (loginAttempted && isLoading) {
            // Wait a bit for the login process to complete
            delay(1000) // Increased delay to ensure DB operation completes

            if (currentUser != null) {
                // Login successful
                isLoading = false
                loginAttempted = false
                onPinEntered()
            } else {
                // Login failed
                isLoading = false
                loginAttempted = false
                Toast.makeText(context, "Invalid username or PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6) pin = it },
                label = { Text("PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && pin.isNotBlank()) {
                        isLoading = true
                        loginAttempted = true
                        userViewModel.login(username, pin)
                    } else {
                        Toast.makeText(context, "Please enter username and PIN.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login")
                }
            }
        }
    }
}