package ppb.eas.digibank.data

import kotlinx.coroutines.flow.Flow

class CardRepository(private val cardDao: CardDao) {
    fun getCardsForUser(userId: Int): Flow<List<Card>> {
        return cardDao.getCardsForUser(userId)
    }

    suspend fun insert(card: Card) {
        cardDao.insert(card)
    }

    suspend fun deleteCardById(cardId: Int) {
        cardDao.deleteCardById(cardId)
    }
}