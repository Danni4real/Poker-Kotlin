
enum class E_PATTERN_BASE { NULL, Q, SINGLE, PAIR, TRIPLE, BOMB, TRIPLE_PLUS_SINGLE, TRIPLE_PLUS_PAIR, BOMB_PLUS_SINGLE, BOMB_PLUS_PAIR}
enum class E_PATTERN_TIMES {x0, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12}

class Pattern(patternBase: E_PATTERN_BASE, patternTimes: E_PATTERN_TIMES) {
    var base = patternBase
    var times = patternTimes

    fun equalTo(pattern: Pattern): Boolean {
        if (pattern.base == base && pattern.times == times)
            return true
        return false
    }

    fun notBomb(): Boolean {
        if (base == E_PATTERN_BASE.BOMB)
            return false
        return true
    }

    fun noPattern(): Boolean {
        if (base == E_PATTERN_BASE.NULL)
            return true
        return false
    }

    fun print() {
        print(base)
        print(times)
        println()
    }
}

fun timesQ(cards: List<Int>): Int {
    if (cards.size < 5)
        return 0

    var itemShouldBe = cards[0]

    for (item in cards) {
        if (item != itemShouldBe++)
            return 0
    }
    return cards.size
}

fun timesPair(cards: List<Int>): Int {
    if (cards.size in 3..5 || cards.size % 2 != 0)
        return 0

    var cardShouldBe = cards[0]

    for (index in cards.indices step 2) {
        if (cards[index] != cards[index+1] || cards[index] != cardShouldBe++)
            return 0
    }

    return cards.size/2
}

fun timesTriple(cards: List<Int>): Int {
    if (cards.size < 3 || cards.size % 3 != 0)
        return 0

    var cardShouldBe = cards[0]

    for (index in cards.indices step 3) {
        if (cards[index] != cards[index+1] || cards[index] != cards[index+2] || cards[index] != cardShouldBe++)
            return 0
    }

    return cards.size/3
}

fun timesTriplePlusSingle(cards: List<Int>): Int {
    if (cards.size < 4 || cards.size % 4 != 0)
        return 0

    var tripleTimes = timesTriple(cards.subList(cards.size/4, cards.size))

    for (index in 0 until cards.size/4)
        if (cards[index] == cards[index+1])
            return 0

    if (cards.size/4 == tripleTimes)
        return cards.size/4

    return 0
}

fun timesTriplePlusPair(cards: List<Int>): Int {
    if (cards.size < 5 || cards.size % 5 != 0)
        return 0

    var tripleTimes = timesTriple(cards.subList(cards.size*2/5, cards.size))

    for (index in 0 until cards.size*2/5 step 2)
        if (cards[index] != cards[index+1])
            return 0

    if (cards.size/5 == tripleTimes)
        return cards.size/5

    return 0
}

fun isSingle(cards: List<Int>): Boolean {
    if (cards.size == 1)
        return true
    return false
}

fun isBomb(cards: List<Int>): Boolean {
    if (cards.size == 4 && cards[0] == cards[1] && cards[0] == cards[2] && cards[0] == cards[3])
        return true
    if (cards.size == 2 && cards[0] == 30 && cards[1] == 40)
        return true

    return false
}

fun isBombPlusSingle(cards: List<Int>): Boolean {
    if (cards.size != 6 || cards[0] == cards[1])
        return false

    return isBomb(cards.subList(2,cards.size))
}

fun isBombPlusPair(cards: List<Int>): Boolean {
    if (cards.size != 8)
        return false
    if (cards[0] != cards[1] || cards[2] != cards[3] || cards[0] == cards[2])
        return false

    return isBomb(cards.subList(4,cards.size))
}
