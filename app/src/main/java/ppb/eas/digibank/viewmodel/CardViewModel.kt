package ppb.eas.digibank.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ppb.eas.digibank.data.AppDatabase
import ppb.eas.digibank.data.Card
import ppb.eas.digibank.data.CardRepository

class CardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CardRepository
    val cards: LiveData<List<Card>>

    init {
        val cardDao = AppDatabase.getDatabase(application).cardDao()
        repository = CardRepository(cardDao)
        cards = repository.allCards
    }

    fun insert(card: Card) = viewModelScope.launch {
        repository.insert(card)
    }

    fun update(card: Card) = viewModelScope.launch {
        repository.update(card)
    }

    fun delete(card: Card) = viewModelScope.launch {
        repository.delete(card)
    }

    fun getCardById(id: Int) = repository.getCardById(id)
}
