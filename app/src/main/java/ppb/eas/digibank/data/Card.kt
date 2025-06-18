package ppb.eas.digibank.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val cardHolderName: String,
    val cardNumber: String,
    val expiryDate: String, // Stored as MM/YY
    val cvv: String,
    val provider: String // e.g., Visa, Mastercard
)