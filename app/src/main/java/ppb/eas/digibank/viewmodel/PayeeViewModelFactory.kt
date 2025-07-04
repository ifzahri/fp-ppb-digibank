package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PayeeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PayeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PayeeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
