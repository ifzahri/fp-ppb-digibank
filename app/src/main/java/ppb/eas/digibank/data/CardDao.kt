package ppb.eas.digibank.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert
    suspend fun insert(card: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("SELECT * from cards ORDER BY id ASC")
    fun getAllCards(): LiveData<List<Card>>

    @Query("SELECT * from cards WHERE id = :id")
    fun getCardById(id: Int): Flow<Card>

    @Query("SELECT * from cards WHERE id = :id")
    suspend fun getCard(id: Int): Card?
}
