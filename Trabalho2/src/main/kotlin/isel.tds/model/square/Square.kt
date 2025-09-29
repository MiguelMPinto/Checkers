package isel.tds.model.square

import isel.tds.model.BOARD_DIM
import model.square.Column
import model.square.Row
import model.square.toColumnOrNull
import model.square.toRowOrNull

class Square (val row: Row, val column: Column){


    val index: Int
        get() = row.index * BOARD_DIM + column.index

    val black: Boolean
        get() = (row.index + column.index) % 2 == 1

    override fun toString(): String {
        return "${BOARD_DIM - row.index}${'a' + column.index}"
    }


    companion object {
        // Cache para armazenar as instâncias de Square já criadas
        private val cache = mutableMapOf<Pair<Int, Int>, Square>()

        // Gera todos os quadrados possíveis e guarda no cache
        var values: List<Square> = buildList {
            for (row in 0 until BOARD_DIM) {
                for (column in 0 until BOARD_DIM) {
                    // Gera os quadrados e adiciona ao cache e à lista
                    add(of(Row(row), Column(column)))
                }
            }
        }

        // Função para obter ou criar uma instância de Square
        fun of(row: Row, column: Column): Square {
            // Verifica se o quadrado já foi criado no cache e retorna a instância
            return cache.getOrPut(Pair(row.index, column.index)) {
                Square(row, column)
            }
        }
    }



    override fun equals(other: Any?): Boolean { // Compara os dois square encontradas de maneiras diferentes ver se valem o mesmo
        return other is Square && this.row == other.row && this.column == other.column
    }

    override fun hashCode(): Int { // Em vez de representar o hascode ele irá representar um index quando lhe pedirem  hascode
        return index.hashCode()
    }

}


fun String.toSquareOrNull(): Square? {
    if (length != 2) return null

    val rowNumber = this[0]
    val columnLetter = this[1]

    val column = columnLetter.toColumnOrNull()
    val row = rowNumber.toRowOrNull()

    return if (row != null && column != null) Square(row, column) else null
}


fun String.toSquare(): Square { // Entrega um square caso não seja possivel manda exeção
    val a = this.toSquareOrNull()

    if (a == null){
        throw IllegalArgumentException("Invalid string")
    }
    return a
}

