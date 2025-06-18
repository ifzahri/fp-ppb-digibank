package ppb.eas.digibank.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PayeeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(payee: Payee)

    @Delete
    suspend fun delete(payee: Payee)

    @Query("SELECT * FROM payees WHERE id_user = :userId ORDER BY name ASC")
    fun getAllPayees(userId: Int): Flow<List<Payee>>
}
