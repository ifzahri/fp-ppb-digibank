package ppb.eas.digibank.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    var balance: Double,
    val accountNumber: String,
    var pin: String? = null // Added PIN field for transaction security
)