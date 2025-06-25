package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.User
import ppb.eas.digibank.data.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        // For demonstration, let's create a default user if one doesn't exist.
        viewModelScope.launch {
            var user = repository.getUserByUsername("ifzahri")
            if (user == null) {
                user = User(username = "ifzahri", pin = "123456", name = "Ifza H.R.")
                repository.insert(user)
            }
        }
    }

    fun login(username: String, pin: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            if (user != null && user.pin == pin) {
                _currentUser.postValue(user)
            } else {
                _currentUser.postValue(null)
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            repository.insert(user)
        }
    }

    /**
     * Check if a given username is not already in the DB.
     * Calls onResult(true) if available, false if already taken.
     */
    fun isUsernameAvailable(username: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            onResult(user == null)
        }
    }

    fun logout() {
        _currentUser.value = null
    }
}
