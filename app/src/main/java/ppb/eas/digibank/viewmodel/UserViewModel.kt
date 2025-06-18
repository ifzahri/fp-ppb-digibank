package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.Transaction
import ppb.eas.digibank.data.TransactionRepository
import ppb.eas.digibank.data.User
import ppb.eas.digibank.data.UserRepository
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(application: Application) : ViewModel() {

    private val userRepository: UserRepository
    private val transactionRepository: TransactionRepository

    // Hardcode user ID for this demo. In a real app, this would come from a login process.
    private val userId = 1

    // Convert the Flow from the database into a StateFlow.
    // This will automatically handle the lifecycle and threading, and it ensures
    // the UI gets updated as soon as the data is loaded.
    val loggedInUser: StateFlow<User?> =
        UserRepository(AppDatabase.getDatabase(application, viewModelScope).userDao())
            .getUser(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    // Also convert the 'allUsers' flow for consistency and efficiency.
    val allUsers: StateFlow<List<User>> =
        UserRepository(AppDatabase.getDatabase(application, viewModelScope).userDao())
            .allUsers
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    private val _transferResult = MutableStateFlow<Result<Unit>?>(null)
    val transferResult: StateFlow<Result<Unit>?> = _transferResult.asStateFlow()

    init {
        // The repositories are still needed for write operations.
        val database = AppDatabase.getDatabase(application, viewModelScope)
        userRepository = UserRepository(database.userDao())
        transactionRepository = TransactionRepository(database.transactionDao())
    }

    fun topUpBalance(amount: Double) = viewModelScope.launch {
        // We can now safely get the user directly from the StateFlow's current value.
        val user = loggedInUser.value
        if (user != null && amount > 0) {
            val updatedUser = user.copy(balance = user.balance + amount)
            userRepository.updateUser(updatedUser)

            val transaction = Transaction(
                userId = user.id,
                amount = amount,
                date = Date(),
                type = "Top Up",
                description = "Added funds to balance"
            )
            transactionRepository.insert(transaction)
        }
    }

    fun transferFunds(recipient: User, amount: Double, enteredPin: String) = viewModelScope.launch {
        val sender = loggedInUser.value

        if (sender == null) {
            _transferResult.value = Result.failure(Exception("Sender not found."))
            return@launch
        }

        if (sender.pin == null) {
            _transferResult.value = Result.failure(Exception("PIN not set. Please set up your PIN first."))
            return@launch
        }

        if (sender.pin != enteredPin) {
            _transferResult.value = Result.failure(Exception("Incorrect PIN."))
            return@launch
        }

        if (sender.balance < amount) {
            _transferResult.value = Result.failure(Exception("Insufficient funds."))
            return@launch
        }

        val updatedSender = sender.copy(balance = sender.balance - amount)
        val updatedRecipient = recipient.copy(balance = recipient.balance + amount)

        userRepository.updateUser(updatedSender)
        userRepository.updateUser(updatedRecipient)

        val senderTransaction = Transaction(
            userId = sender.id,
            amount = -amount,
            date = Date(),
            type = "Transfer Out",
            description = "Transfer to ${recipient.name}"
        )
        val recipientTransaction = Transaction(
            userId = recipient.id,
            amount = amount,
            date = Date(),
            type = "Transfer In",
            description = "Transfer from ${sender.name}"
        )
        transactionRepository.insert(senderTransaction)
        transactionRepository.insert(recipientTransaction)

        _transferResult.value = Result.success(Unit)
    }

    fun setPin(newPin: String) = viewModelScope.launch {
        val user = loggedInUser.value
        if (user != null) {
            val updatedUser = user.copy(pin = newPin)
            userRepository.updateUser(updatedUser)
        }
    }

    fun resetTransferResult() {
        _transferResult.value = null
    }
}