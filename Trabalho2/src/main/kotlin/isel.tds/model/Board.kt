package isel.tds.model

import isel.tds.AppProperties
import isel.tds.model.square.Square
import model.square.Column
import model.square.Row
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

val BOARD_DIM = AppProperties.p.getProperty("BOARD_SIZE")?.toInt() ?: 8
typealias Moves = MutableMap<Square, Piece?> //= mutableMapOf()


sealed class Board(val moves: Moves) {
    override fun equals(other: Any?) = other is Board && isEquals(other)
    override fun hashCode() = moves.hashCode() + hashAdd()
}

class BoardRun(moves: Moves = setupPieces(), var turn: Piece) : Board(moves) {
}

class BoardWin(moves: Moves, val winner: Piece) : Board(moves) {
    var win: Piece = winner
}

class BoardDraw(moves: Moves) : Board(moves)


fun Board.isEquals(other: Board): Boolean =
    when (this) {
        is BoardRun -> other is BoardRun && turn == other.turn
        is BoardWin -> other is BoardWin && winner == other.winner
        is BoardDraw -> other is BoardDraw
    } && moves == other.moves


private fun Board.hashAdd(): Int = when (this) {
    is BoardRun -> turn.ordinal
    is BoardWin -> winner.ordinal
    is BoardDraw -> 0
}


fun setupPieces(): Moves {
    val mapa: Moves = mutableMapOf()

    // Número de linhas para peças pretas e brancas
    val numRows = when (BOARD_DIM) {
        4 -> 1 // Apenas 1 linha para peças em tabuleiros pequenos
        6 -> 2 // 2 linhas para tabuleiros médios
        else -> 3 // 3 linhas para tabuleiros maiores (padrão)
    }

    // Configuração para peças pretas nas primeiras linhas
    for (rowIndex in 0 until numRows) {
        for (colIndex in 0 until BOARD_DIM) {
            val square = Square(Row(rowIndex), Column(colIndex))
            if (square.black) {
                mapa[square] = Piece.Black
            }
        }
    }

    // Configuração para peças brancas nas últimas linhas
    for (rowIndex in (BOARD_DIM - numRows) until BOARD_DIM) {
        for (colIndex in 0 until BOARD_DIM) {
            val square = Square(Row(rowIndex), Column(colIndex))
            if (square.black) {
                mapa[square] = Piece.White
            }
        }
    }

    // Configuração para as casas jogáveis "-" no meio
    for (rowIndex in numRows until (BOARD_DIM - numRows)) {
        for (colIndex in 0 until BOARD_DIM) {
            val square = Square(Row(rowIndex), Column(colIndex))
            if (square.black) {
                mapa[square] = Piece.Empty // Casas jogáveis são marcadas como "-"
            }
        }
    }

    return mapa
}


fun BoardRun.canMoveAsQueen(from: Square, to: Square): Boolean {
    val fromPiece = moves[from]
    if (fromPiece?.isQueen != true) return false

    val rowDiff = (from.row.index - to.row.index).absoluteValue
    val colDiff = (from.column.index - to.column.index).absoluteValue

    if (rowDiff == colDiff) { // Movimento diagonal
        val stepRow = if (to.row.index > from.row.index) 1 else -1
        val stepCol = if (to.column.index > from.column.index) 1 else -1
        var row = from.row.index + stepRow
        var col = from.column.index + stepCol
        while (row != to.row.index && col != to.column.index) {
            if (Square(Row(row), Column(col)) != null) return false
            row += stepRow
            col += stepCol
        }
        return true
    }
    return false
}

fun BoardRun.isOcupied(from: Square): Boolean = moves[from]!!.color != '-'


fun BoardRun.nextStep(from: Square, to: Square): Boolean { // Retorna True caso nextstep seja possivel
    var a = false
    val fromPiece = moves[from]
    val toPiece = moves[to]
    isQueen(from, to)


    if (fromPiece == null) {
        println("Erro: Não há peça na posição de origem $from.")
        return false
    }

    if (toPiece == null) {
        println("Erro: A posição de destino $to já está ocupada.")
        return false
    }
    if ((moves[from] == Piece.White && to.row.index > from.row.index)) {
        return false
    } else if ((moves[from] == Piece.Black && to.row.index < from.row.index)) {
        return false
    }

    if (!isOcupied(to) && (from.column.index - to.column.index).absoluteValue == 1 &&
        (from.row.index - to.row.index).absoluteValue == 1
    ) { // Next lugar não está ocupado
        a = true
    }

    return a

}


fun BoardRun.isOcupiedByDiffrentPlayer(from: Square, to: Square): Boolean {
    val toPiece = moves[to]
    val fromPiece = moves[from]

    // Se não houver peça no destino ou na origem, retorna falso
    if (toPiece == null || fromPiece == null) return false

    // Verifica se 'fromPiece' é 'w' e 'toPiece' é 'b', ou se 'fromPiece' é 'b' e 'toPiece' é 'w'
    return (fromPiece.color == 'w' && toPiece.color == 'b') ||
            (fromPiece.color == 'b' && toPiece.color == 'w')
}


fun BoardRun.canEat_by1(from: Square, to: Square): Boolean {
    val rowDifference = to.row.index - from.row.index
    val columnDifference = to.column.index - from.column.index

    val finalRow = from.row.index + 2 * rowDifference
    val finalCol = from.column.index + 2 * columnDifference

    val d = try { // 4
        Square(Row(finalRow), Column(finalCol))
    } catch (e: IllegalArgumentException) {
        null
    }
    if (d != null && isOcupiedByDiffrentPlayer(
            from,
            to
        ) && !isOcupied(d)
    ) { // Como estamos a comer com 1 o do meio é o to que foi dado
        if (canEat(from, d) == true) {
            return true
        }
    }
    return false
}


fun newBoard(): BoardRun {
    return BoardRun(turn = Piece.White)
}


fun BoardRun.canEat(from: Square, to: Square): Boolean {
    // A jogada deve ser um salto de duas casas na diagonal
    val rowDifference = from.row.index - to.row.index
    val columnDifference = from.column.index - to.column.index

    if (rowDifference.absoluteValue == 2 && columnDifference.absoluteValue == 2
    ) {

        // Determina a posição intermediária (onde a peça adversária estaria)
        val midRow = (from.row.index + to.row.index) / 2
        val midColumn = (from.column.index + to.column.index) / 2
        val middleSquare = Square(Row(midRow), Column(midColumn))

        // Verifica se a peça intermediária é do adversário usando isOcupiedByDifferentPlayer
        if (isOcupiedByDiffrentPlayer(from, middleSquare) && !isOcupied(to)) {
            return true
        }
    } else if (rowDifference.absoluteValue == 1 && columnDifference.absoluteValue == 1
    ) {
        return canEat_by1(from, to)
    }
    return false
}


fun BoardRun.isQueen(from: Square, to: Square): Boolean {
    val piece = moves[to] ?: return false
    // Verifica promoção para peças brancas
    if (to.row.index == 0 && piece.color == 'w') {
        moves[to] = Piece.DamaWhite
        return true
    }
    // Verifica promoção para peças pretas
    if (to.row.index == BOARD_DIM - 1 && piece.color == 'b') {
        moves[to] = Piece.DamaBlack
        return true
    }
    return false
}


fun BoardRun.playMove(from: Square, to: Square) {
    val fromPiece = moves[from] ?: return // Verifica se há uma peça na origem
    // Primeiro, verifica se a peça deve ser promovida
    if (isQueen(from, to)) {
        // Se a peça foi promovida, atualiza o destino diretamente com a peça promovida
        moves[to] = moves[from] // A peça já foi promovida em isQueen
    } else {
        // Caso contrário, move a peça normalmente
        moves[to] = fromPiece
    }
    // Esvazia a posição original
    moves[from] = Piece.Empty
}


fun BoardRun.undomove(from: Square, to: Square) {

    val midRow = (from.row.index + to.row.index) / 2
    val midColumn = (from.column.index + to.column.index) / 2
    var mid = moves[to]
    mid = if (mid == Piece.White) Piece.Black else Piece.White

    //Mete a peça que veio de se mexer no from
    moves[from] = moves[to]
    //Mete uma peça da cor contraria à do to
    moves[Square(Row(midRow), Column(midColumn))] = mid
    //Mete um espaço vazio
    moves[to] = Piece.Empty
}


fun BoardRun.eatPieces(from: Square, to: Square) {
    val midRow = (from.row.index + to.row.index) / 2
    val midCol = (from.column.index + to.column.index) / 2
    // Remove a peça adversária
    moves[Square(Row(midRow), Column(midCol))] = Piece.Empty
    // Move a peça para o destino
    moves[to] = moves[from]
    // Define a posição original como vazia
    moves[from] = Piece.Empty
    // Verifica se a peça no destino está na última linha (branca) ou na primeira linha (preta)
    if ((to.row.index == 0 && moves[to]?.color == 'w') ||
        (to.row.index == BOARD_DIM - 1 && moves[to]?.color == 'b')
    ) {
        isQueen(from, to) // Chama isQueen para promover a peça
    }
}
