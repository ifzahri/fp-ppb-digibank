package ppb.eas.digibank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val senderId: Int,
    val receiverId: Int,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
