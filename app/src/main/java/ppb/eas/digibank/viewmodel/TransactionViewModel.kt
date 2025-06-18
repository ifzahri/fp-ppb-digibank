package ppb.eas.digibank.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ppb.eas.digibank.data.Transaction
import ppb.eas.digibank.data.TransactionRepository
import ppb.eas.digibank.data.User
import ppb.eas.digibank.data.UserRepository

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun insert(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.insert(transaction)
    }

    fun getTransactionsForUser(userId: Int) =
        transactionRepository.getTransactionsForUser(userId).asLiveData()

    fun getAllTransactions() = transactionRepository.getAllTransactions().asLiveData()

    suspend fun transferMoney(
        sender: User,
        receiverUsername: String,
        amount: Double
    ): Boolean {
        if (sender.balance < amount) {
            return false // Insufficient funds
        }

        val receiver = userRepository.getUserByUsername(receiverUsername).asLiveData().value

        if (receiver == null) {
            return false // Receiver not found
        }

        viewModelScope.launch {
            val updatedSender = sender.copy(balance = sender.balance - amount)
            userRepository.update(updatedSender)

            val updatedReceiver = receiver.copy(balance = receiver.balance + amount)
            userRepository.update(updatedReceiver)

            val transaction = Transaction(
                senderId = sender.id,
                receiverId = receiver.id,
                amount = amount
            )
            transactionRepository.insert(transaction)
        }
        return true
    }
}

class TransactionViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(transactionRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
