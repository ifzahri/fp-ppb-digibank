package ppb.eas.digibank.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Database(entities = [User::class, Transaction::class, Card::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digibank_database"
                )
                    .fallbackToDestructiveMigration() // Destroys and rebuilds database on version change
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.userDao(), database.transactionDao())
                    }
                }
            }

            // Repopulate with sample data
            suspend fun populateDatabase(userDao: UserDao, transactionDao: TransactionDao) {
                userDao.deleteAll()
                transactionDao.deleteAll()

                val user1 = User(id = 1, name = "John Doe", balance = 1000.0, accountNumber = "1234567890", pin = "1234")
                val user2 = User(id = 2, name = "Jane Smith", balance = 500.0, accountNumber = "0987654321", pin = "5678")
                userDao.insert(user1)
                userDao.insert(user2)

                transactionDao.insert(Transaction(userId = 1, amount = 1000.0, date = Date(), type = "Initial Deposit", description = "Account opening balance"))
                transactionDao.insert(Transaction(userId = 2, amount = 500.0, date = Date(), type = "Initial Deposit", description = "Account opening balance"))
            }
        }
    }
}