package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.Payee
import ppb.eas.digibank.data.PayeeRepository

class PayeeViewModel(application: Application) : AndroidViewModel(application) {
    private val payeeRepository: PayeeRepository
    val payees: Flow<List<Payee>>

    init {
        val payeeDao = AppDatabase.getDatabase(application).payeeDao()
        payeeRepository = PayeeRepository(payeeDao)
        payees = payeeRepository.getAllPayees(1) // Assuming user id 1
    }

    fun insert(payee: Payee) = viewModelScope.launch {
        payeeRepository.insert(payee)
    }

    fun delete(payee: Payee) = viewModelScope.launch {
        payeeRepository.delete(payee)
    }
}
