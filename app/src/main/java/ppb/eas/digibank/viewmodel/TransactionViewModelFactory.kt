package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.CardRepository

class TransactionViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            val cardDao = AppDatabase.getDatabase(application).cardDao()
            val cardRepository = CardRepository(cardDao)
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(application, cardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
