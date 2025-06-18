package ppb.eas.digibank.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "id_card")
    val id_card: Int,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "date")
    val date: Date
)