package isel.tds.model.square

import isel.tds.model.BOARD_DIM

val BOARD_CELLS = BOARD_DIM * BOARD_DIM

@JvmInline
value class Position private constructor(val index: Int) {
    val row: Int get() = index / BOARD_DIM  // row in (0..<BOARD_SIZE)
    val col: Int get() = index % BOARD_DIM  // col in (0..<BOARD_SIZE)
    val backSlash get() = row == col         // Is in principal diagonal \
    val slash get() = row+col == BOARD_DIM - 1// Is in secondary diagonal /


    override fun toString() = "$index"
    companion object {
        val values = List(BOARD_CELLS) { Position(it) } // All positions
        operator fun invoke(index: Int): Position =
            values[index] // Can throw IndexOutOfBounds
    }
}

fun Position(row: Int, col: Int): Position {
    require(row in 0..<BOARD_DIM && col in 0..<BOARD_DIM)
    return Position.values[row * BOARD_DIM + col]
}