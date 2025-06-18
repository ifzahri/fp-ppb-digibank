package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.Transaction
import ppb.eas.digibank.data.TransactionRepository

class TransactionViewModel(application: Application, userId: Int) : ViewModel() {
    private val transactionRepository: TransactionRepository
    val userTransactions: Flow<List<Transaction>>

    init {
        val transactionDao = AppDatabase.getDatabase(application, viewModelScope).transactionDao()
        transactionRepository = TransactionRepository(transactionDao)
        userTransactions = transactionRepository.getTransactionsForUser(userId)
    }
}