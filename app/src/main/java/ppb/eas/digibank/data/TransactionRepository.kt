package ppb.eas.digibank.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    fun getTransactionsForUser(userId: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForUser(userId)
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }
}
