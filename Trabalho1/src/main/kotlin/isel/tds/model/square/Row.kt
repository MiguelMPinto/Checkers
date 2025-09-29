package model.square

import isel.tds.model.BOARD_DIM

@JvmInline
value class Row (val index: Int){
    init {
        require(index in 0 until BOARD_DIM) { "Invalid row index: $index" } //Verifica que o index é valido
    }

    val digit: Char
        get() = (BOARD_DIM - index).digitToChar() //Cria o Digit

    companion object {
        var values: List<Row> = (0 until BOARD_DIM).map { Row(it) }
    }

}


fun Char.toRowOrNull(): Row? {
    if (this.isDigit()) {
        val digitValue = this.digitToInt()
        for (i in 0..<BOARD_DIM) {
            if (i == BOARD_DIM - digitValue) { // Verifica que se o this pertençe ao board
                return Row(i)
            }
        }
    }
    return null
}
