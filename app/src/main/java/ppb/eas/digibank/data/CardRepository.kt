package ppb.eas.digibank.data

import androidx.lifecycle.LiveData

class CardRepository(private val cardDao: CardDao) {
    val allCards: LiveData<List<Card>> = cardDao.getAllCards()

    suspend fun insert(card: Card) {
        cardDao.insert(card)
    }

    suspend fun update(card: Card) {
        cardDao.update(card)
    }

    suspend fun delete(card: Card) {
        cardDao.delete(card)
    }

    fun getCardById(id: Int) = cardDao.getCardById(id)

    suspend fun getCard(id: Int): Card? = cardDao.getCard(id)
}
