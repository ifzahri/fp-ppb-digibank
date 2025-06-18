package ppb.eas.digibank.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "id_user")
    val id_user: Int,
    @ColumnInfo(name = "card_number")
    val cardNumber: String,
    @ColumnInfo(name = "card_holder_name")
    val cardHolderName: String,
    @ColumnInfo(name = "expiry_date")
    val expiryDate: String,
    @ColumnInfo(name = "cvv")
    val cvv: String,
    @ColumnInfo(name = "balance")
    var balance: Double,
    @ColumnInfo(name = "card_type")
    val cardType: String,
    @ColumnInfo(name = "pin")
    val pin: String
)
