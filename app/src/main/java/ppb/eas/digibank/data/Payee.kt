package ppb.eas.digibank.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payees")
data class Payee(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "id_user")
    val id_user: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "bank_name")
    val bankName: String,
    @ColumnInfo(name = "account_number")
    val accountNumber: String
)
