package ppb.eas.digibank.data

import androidx.lifecycle.LiveData

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTransactionsByCard(cardId: Int): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByCard(cardId)
    }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
}
