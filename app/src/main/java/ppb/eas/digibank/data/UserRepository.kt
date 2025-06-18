package ppb.eas.digibank.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    fun getUser(id: Int): Flow<User>{
        return userDao.getUser(id)
    }

    // Updated to match the change in UserDao
    fun getUserByName(name: String): Flow<User>{
        return userDao.getUserByName(name)
    }
}