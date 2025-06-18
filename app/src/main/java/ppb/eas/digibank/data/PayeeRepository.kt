package ppb.eas.digibank.data

import kotlinx.coroutines.flow.Flow

class PayeeRepository(private val payeeDao: PayeeDao) {
    fun getAllPayees(userId: Int): Flow<List<Payee>> = payeeDao.getAllPayees(userId)
    suspend fun insert(payee: Payee) = payeeDao.insert(payee)
    suspend fun delete(payee: Payee) = payeeDao.delete(payee)
}
