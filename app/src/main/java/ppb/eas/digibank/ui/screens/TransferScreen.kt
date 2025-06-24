package ppb.eas.digibank.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.data.Payee
import ppb.eas.digibank.ui.components.BackButton
import ppb.eas.digibank.viewmodel.CardViewModel
import ppb.eas.digibank.viewmodel.CardViewModelFactory
import ppb.eas.digibank.viewmodel.PayeeViewModel
import ppb.eas.digibank.viewmodel.PayeeViewModelFactory
import ppb.eas.digibank.viewmodel.TransactionViewModel
import ppb.eas.digibank.viewmodel.TransactionViewModelFactory
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    navController: NavHostController,
    cardViewModel: CardViewModel = viewModel(factory = CardViewModelFactory(LocalContext.current.applicationContext as Application)),
    transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(LocalContext.current.applicationContext as Application)),
    payeeViewModel: PayeeViewModel = viewModel(factory = PayeeViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val context = LocalContext.current
    val cards by cardViewModel.cards.observeAsState(initial = emptyList())
    val payees by payeeViewModel.payees.collectAsState(initial = emptyList())

    var fromCard by remember { mutableStateOf<Card?>(null) }
    var selectedPayee by remember { mutableStateOf<Payee?>(null) }
    var toBank by remember { mutableStateOf("") }
    var toAccNumber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var fromCardExpanded by remember { mutableStateOf(false) }
    var payeeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transfer to Other Bank",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    BackButton(navController = navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HeaderSection()
            }

            item {
                SourceCardSection(
                    cards = cards,
                    fromCard = fromCard,
                    fromCardExpanded = fromCardExpanded,
                    onFromCardExpandedChange = { fromCardExpanded = it },
                    onFromCardSelected = { fromCard = it }
                )
            }

            item {
                PayeeSection(
                    payees = payees,
                    selectedPayee = selectedPayee,
                    payeeExpanded = payeeExpanded,
                    onPayeeExpandedChange = { payeeExpanded = it },
                    onPayeeSelected = { payee ->
                        selectedPayee = payee
                        if (payee != null) {
                            toBank = payee.bankName
                            toAccNumber = payee.accountNumber
                        } else {
                            toBank = ""
                            toAccNumber = ""
                        }
                    }
                )
            }

            item {
                RecipientDetailsSection(
                    toBank = toBank,
                    onToBankChange = { toBank = it },
                    toAccNumber = toAccNumber,
                    onToAccNumberChange = { toAccNumber = it },
                    isReadOnly = selectedPayee != null
                )
            }

            item {
                AmountSection(
                    amount = amount,
                    onAmountChange = { amount = it }
                )
            }

            item {
                TransferButton(
                    fromCard = fromCard,
                    toBank = toBank,
                    toAccNumber = toAccNumber,
                    amount = amount,
                    onTransfer = {
                        val from = fromCard
                        val transferAmount = amount.toDoubleOrNull()

                        if (from == null || toBank.isBlank() || toAccNumber.isBlank() || transferAmount == null || transferAmount <= 0) {
                            Toast.makeText(context, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
                            return@TransferButton
                        }

                        transactionViewModel.transfer(
                            fromCardId = from.id,
                            toBank = toBank,
                            toAccNumber = toAccNumber,
                            amount = transferAmount,
                            onSuccess = {
                                Toast.makeText(context, "Transfer successful!", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, "Transfer failed: $errorMsg", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    val gradientColors = listOf(Color(0xFF2196F3), Color(0xFF03DAC6))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors)
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Transfer to Other Bank",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Send money to any bank account",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourceCardSection(
    cards: List<Card>,
    fromCard: Card?,
    fromCardExpanded: Boolean,
    onFromCardExpandedChange: (Boolean) -> Unit,
    onFromCardSelected: (Card) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Source Card",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ExposedDropdownMenuBox(
                expanded = fromCardExpanded,
                onExpandedChange = onFromCardExpandedChange
            ) {
                OutlinedTextField(
                    value = fromCard?.let {
                        "**** ${it.cardNumber.takeLast(4)} | Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(it.balance)}"
                    } ?: "Select source card",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = fromCardExpanded,
                    onDismissRequest = { onFromCardExpandedChange(false) }
                ) {
                    cards.forEach { card ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = "**** ${card.cardNumber.takeLast(4)}",
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Balance: Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(card.balance)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            onClick = {
                                onFromCardSelected(card)
                                onFromCardExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PayeeSection(
    payees: List<Payee>,
    selectedPayee: Payee?,
    payeeExpanded: Boolean,
    onPayeeExpandedChange: (Boolean) -> Unit,
    onPayeeSelected: (Payee?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Recipient",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ExposedDropdownMenuBox(
                expanded = payeeExpanded,
                onExpandedChange = onPayeeExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedPayee?.name ?: "Select from saved payees or enter manually",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = payeeExpanded,
                    onDismissRequest = { onPayeeExpandedChange(false) }
                ) {
                    payees.forEach { payee ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = payee.name,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${payee.bankName} - ${payee.accountNumber}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            onClick = {
                                onPayeeSelected(payee)
                                onPayeeExpandedChange(false)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Enter manually...",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            onPayeeSelected(null)
                            onPayeeExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipientDetailsSection(
    toBank: String,
    onToBankChange: (String) -> Unit,
    toAccNumber: String,
    onToAccNumberChange: (String) -> Unit,
    isReadOnly: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Recipient Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Bank Name",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = toBank,
                onValueChange = onToBankChange,
                placeholder = { Text("Enter bank name") },
                readOnly = isReadOnly,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Account Number",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = toAccNumber,
                onValueChange = onToAccNumberChange,
                placeholder = { Text("Enter account number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = isReadOnly,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun AmountSection(
    amount: String,
    onAmountChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Transfer Amount",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                placeholder = { Text("Enter amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                prefix = { Text("Rp ") }
            )
        }
    }
}

@Composable
private fun TransferButton(
    fromCard: Card?,
    toBank: String,
    toAccNumber: String,
    amount: String,
    onTransfer: () -> Unit
) {
    val isEnabled = fromCard != null && toBank.isNotBlank() && toAccNumber.isNotBlank() && amount.isNotBlank()

    Button(
        onClick = onTransfer,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Transfer Now",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}