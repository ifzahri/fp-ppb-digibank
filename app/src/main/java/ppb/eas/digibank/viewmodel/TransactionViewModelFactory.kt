package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TransactionViewModelFactory(private val application: Application, i: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // This assumes a default user ID of 1 for fetching transactions.
            // In a real app, this would be dynamic.
            return TransactionViewModel(application, 1) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}