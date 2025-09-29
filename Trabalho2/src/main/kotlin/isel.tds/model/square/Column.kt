package model.square

import isel.tds.model.BOARD_DIM

val abecedario = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')


@JvmInline
value class Column(val index: Int) {
    init {
        require(index in 0 until BOARD_DIM) { "Invalid column index: $index" } //Verifica que o index é valido
    }

    val symbol: Char
        get() = abecedario[index] //Cria o symbol


    companion object {
        var values: List<Column> = (0 until BOARD_DIM).map { Column(it) }
    }

}

fun Char.toColumnOrNull(): Column? {
    for (i in 0..<BOARD_DIM){
        if (abecedario[i] == this){ // Verifica que se o this pertençe ao board
            return Column(i)
        }
    }
    return null
}
