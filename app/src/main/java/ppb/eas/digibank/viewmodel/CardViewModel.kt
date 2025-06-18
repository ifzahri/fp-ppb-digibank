package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.data.CardRepository

class CardViewModel(application: Application, private val userId: Int) : ViewModel() {
    private val cardRepository: CardRepository

    val userCards: Flow<List<Card>>

    init {
        val cardDao = AppDatabase.getDatabase(application, viewModelScope).cardDao()
        cardRepository = CardRepository(cardDao)
        userCards = cardRepository.getCardsForUser(userId)
    }

    fun addCard(card: Card) = viewModelScope.launch {
        cardRepository.insert(card)
    }

    fun deleteCard(cardId: Int) = viewModelScope.launch {
        cardRepository.deleteCardById(cardId)
    }
}

class CardViewModelFactory(private val application: Application, private val userId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(application, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}