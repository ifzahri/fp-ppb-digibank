package ppb.eas.digibank.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    fun getUserById(id: Int): Flow<User> {
        return userDao.getUserById(id)
    }
}
