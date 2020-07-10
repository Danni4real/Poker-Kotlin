import java.util.*
import kotlin.system.exitProcess

val POKER_DECK = listOf(1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4,5,5,5,5,6,6,6,6,7,7,7,7,8,8,8,8,9,9,9,9,10,10,10,10,11,11,11,11,12,12,12,12,20,20,20,20,30,40)
val POKER_VALUE_LIST = listOf( 1,  2,  3,  4,  5,  6,  7,  8,   9,  10, 11, 12, 20, 30, 40)
val POKER_NAME_LIST  = listOf("3","4","5","6","7","8","9","10","J","Q","K","A","2","B","R")

class Cards(list: List<Int>) {
    private var valueList = list.sorted()
    private var bossCard = 0
    private var pattern = Pattern(E_PATTERN_BASE.NULL, E_PATTERN_TIMES.x0)

    private fun getBossCard() {
        bossCard = valueList.last()
    }

    private fun sortByPattern() {
        var singleList = mutableListOf<Int>()
        var pairList = mutableListOf<Int>()
        var tripleList = mutableListOf<Int>()
        var bombList = mutableListOf<Int>()
        var newValueList = mutableListOf<Int>()

        var valueSet = mutableSetOf<Int>()

        for (item in valueList)
            valueSet.add(item)

        for (itemInSet in valueSet) {
            var times = 0
            for (itemInList in valueList) {
                if (itemInSet == itemInList)
                    times++
            }
            when (times) {
                1 -> singleList.add(itemInSet)
                2 -> pairList.add(itemInSet)
                3 -> tripleList.add(itemInSet)
                4 -> bombList.add(itemInSet)
            }
        }

        for(item in singleList.sorted())
            newValueList.add(item)
        for(item in pairList.sorted()) {
            newValueList.add(item)
            newValueList.add(item)
        }
        for(item in tripleList.sorted()) {
            newValueList.add(item)
            newValueList.add(item)
            newValueList.add(item)
        }
        for(item in bombList.sorted()) {
            newValueList.add(item)
            newValueList.add(item)
            newValueList.add(item)
            newValueList.add(item)
        }

        valueList = newValueList
    }

    fun getPattern(): Pattern {
        sortByPattern()
        when {
            timesQ(valueList) > 0                -> pattern = Pattern(E_PATTERN_BASE.Q,                  E_PATTERN_TIMES.values()[timesQ(valueList)])
            timesPair(valueList) > 0             -> pattern = Pattern(E_PATTERN_BASE.PAIR,               E_PATTERN_TIMES.values()[timesPair(valueList)])
            timesTriple(valueList) > 0           -> pattern = Pattern(E_PATTERN_BASE.TRIPLE,             E_PATTERN_TIMES.values()[timesTriple(valueList)])
            timesTriplePlusSingle(valueList) > 0 -> pattern = Pattern(E_PATTERN_BASE.TRIPLE_PLUS_SINGLE, E_PATTERN_TIMES.values()[timesTriplePlusSingle(valueList)])
            timesTriplePlusPair(valueList) > 0   -> pattern = Pattern(E_PATTERN_BASE.TRIPLE_PLUS_PAIR,   E_PATTERN_TIMES.values()[timesTriplePlusPair(valueList)])
            isSingle(valueList)                  -> pattern = Pattern(E_PATTERN_BASE.SINGLE,             E_PATTERN_TIMES.x1)
            isBomb(valueList)                    -> pattern = Pattern(E_PATTERN_BASE.BOMB,               E_PATTERN_TIMES.x1)
            isBombPlusSingle(valueList)          -> pattern = Pattern(E_PATTERN_BASE.BOMB_PLUS_SINGLE,   E_PATTERN_TIMES.x1)
            isBombPlusPair(valueList)            -> pattern = Pattern(E_PATTERN_BASE.BOMB_PLUS_PAIR,     E_PATTERN_TIMES.x1)
            else                                 -> println("Invalid pattern!")
        }
        if (!pattern.noPattern())
            getBossCard()

        return pattern
    }

    fun removeSucceed(subCards: Cards): Boolean {
        var valueListCopy = valueList.toList()

        subCards.valueList.forEach() {
            if (it in valueListCopy)
                valueListCopy -= it
            else {
                println("Invalid card: ${value2name(it)}")
                return false
            }
        }

        valueList = valueListCopy
        return true
    }

    fun biggerThan(cards: Cards): Boolean {
        if (cards.pattern.equalTo(pattern) && bossCard > cards.bossCard)
            return true
        if (cards.pattern.notBomb() && !pattern.notBomb())
            return true
        if (cards.pattern.noPattern() && !pattern.noPattern())
            return true
        return false
    }

    fun isEmpty(): Boolean {
        return valueList.isEmpty()
    }

    fun show() {
        valueList.forEach() {
            print(value2name(it))
        }
        println()
    }
}

fun name2value(name: String): Int =  POKER_VALUE_LIST[POKER_NAME_LIST.indexOf(name)]

fun value2name(value: Int): String =  POKER_NAME_LIST[POKER_VALUE_LIST.indexOf(value)]

fun input2cards(str: String): List<Int> {
    var cards = mutableListOf<Int>()

    for(i in str.indices) {
        when {
            str[i] == '1' && str.length > i+1 && str[i+1] == '0' -> cards.add(8)
            str[i] == '0' && i > 0            && str[i-1] == '1' -> null
            str[i].toString() in POKER_NAME_LIST                 -> cards.add(name2value(str[i].toString()))
            else -> {
                println("Invalid card: ${str[i]}")
                return emptyList()
            }
        }
    }
    return cards
}

fun main() {
    val deckRandom = POKER_DECK.shuffled()
    var maryCards = Cards(deckRandom.subList(0,17))
    var johnCards = Cards(deckRandom.subList(17,34))
    var lordCards = Cards(deckRandom.subList(34,54))
    var inputCards = Cards(emptyList())
    var scanner = Scanner(System.`in`)
    var input: String
    var lastRoundPlay = Cards(emptyList())
    var currentPlayerCards = Cards(emptyList())
    var giveupRoundNum = 0

    while (true) {
        when {
            currentPlayerCards.isEmpty() -> currentPlayerCards = lordCards
            currentPlayerCards == lordCards -> currentPlayerCards = maryCards
            currentPlayerCards == maryCards -> currentPlayerCards = johnCards
            currentPlayerCards == johnCards -> currentPlayerCards = lordCards
        }

        if (giveupRoundNum == 2) {
            giveupRoundNum = 0
            lastRoundPlay = Cards(emptyList())
        }

        while (true) {
            println("\nYour turn to play:")
            print("Cards in pool: ")
            lastRoundPlay.show()
            print("Cards in hand: ")
            currentPlayerCards.show()
            input = scanner.nextLine()

            if (input.isEmpty()) {
                ++giveupRoundNum
                break
            }

            if (input2cards(input).isNotEmpty()) {
                inputCards = Cards(input2cards(input))
                if (!inputCards.getPattern().noPattern() && inputCards.biggerThan(lastRoundPlay) && currentPlayerCards.removeSucceed(inputCards)) {
                    currentPlayerCards.show()
                    lastRoundPlay = inputCards
                    giveupRoundNum = 0
                    if (currentPlayerCards.isEmpty()) {
                        println("You Win!!!")
                        exitProcess(0)
                    }
                    break
                }
            }
            println("Invalid play, try again!")
        }
    }
}
