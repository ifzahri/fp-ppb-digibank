package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.CardRepository
import ppb.eas.digibank.data.Transaction
import ppb.eas.digibank.data.TransactionRepository
import java.util.Date

class TransactionViewModel(
    application: Application,
    private val cardRepository: CardRepository
) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        transactionRepository = TransactionRepository(transactionDao)
        allTransactions = transactionRepository.allTransactions
    }

    fun getTransactionsByCard(id: Int): LiveData<List<Transaction>> {
        return transactionRepository.getTransactionsByCard(id)
    }

    private fun insert(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.insert(transaction)
    }

    fun transfer(fromCardId: Int, toBank: String, toAccNumber: String, amount: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val fromCard = cardRepository.getCard(fromCardId)

            if (fromCard != null) {
                if (fromCard.balance >= amount) {
                    val updatedFromCard = fromCard.copy(balance = fromCard.balance - amount)
                    cardRepository.update(updatedFromCard)

                    val transaction = Transaction(
                        id_card = fromCard.id,
                        amount = -amount,
                        type = "Transfer to $toBank - $toAccNumber",
                        date = Date()
                    )
                    insert(transaction)
                    onSuccess()
                } else {
                    onError("Insufficient funds")
                }
            } else {
                onError("Source card not found")
            }
        }
    }

    fun transferBetweenCards(fromCardId: Int, toCardId: Int, amount: Double, pin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val fromCard = cardRepository.getCard(fromCardId)
            val toCard = cardRepository.getCard(toCardId)

            if (fromCard != null && toCard != null) {
                if (fromCard.pin != pin) {
                    onError("Incorrect PIN")
                    return@launch
                }

                if (fromCard.balance >= amount) {
                    val updatedFromCard = fromCard.copy(balance = fromCard.balance - amount)
                    val updatedToCard = toCard.copy(balance = toCard.balance + amount)

                    cardRepository.update(updatedFromCard)
                    cardRepository.update(updatedToCard)

                    val fromTransaction = Transaction(
                        id_card = fromCard.id,
                        amount = -amount,
                        type = "Transfer to card ..${toCard.cardNumber.takeLast(4)}",
                        date = Date()
                    )
                    val toTransaction = Transaction(
                        id_card = toCard.id,
                        amount = amount,
                        type = "Transfer from card ..${fromCard.cardNumber.takeLast(4)}",
                        date = Date()
                    )
                    insert(fromTransaction)
                    insert(toTransaction)
                    onSuccess()
                } else {
                    onError("Insufficient funds")
                }
            } else {
                onError("Card not found")
            }
        }
    }
}
