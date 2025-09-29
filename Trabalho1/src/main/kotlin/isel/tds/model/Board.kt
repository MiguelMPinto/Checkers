package isel.tds.model

import isel.tds.AppProperties
import isel.tds.model.square.Square
import model.square.Column
import model.square.Row
import kotlin.math.absoluteValue

val BOARD_DIM = AppProperties.p.getProperty("BOARD_SIZE")?.toInt() ?: 3
typealias Moves = MutableMap<Square, Piece?> //= mutableMapOf()
//TODO: Temos de passar de mutable map para imutable map
//TODO: Mesmo problema para o Board que é mutavel



sealed class Board(val moves: Moves) {
    override fun equals(other: Any?) = other is Board && isEquals(other)
    override fun hashCode() = moves.hashCode() + hashAdd()
}
class BoardRun(moves: Moves = setupPieces(), var turn: Piece) : Board(moves) {
}

class BoardWin(moves: Moves, val winner: Piece) : Board(moves){
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


fun setupPieces() : Moves {
    // Peças pretas nas 3 primeiras fileiras (linhas 0 a 2)

    val mapa : Moves = mutableMapOf()

    for (rowIndex in 0 until BOARD_DIM) {
        for (colIndex in 0 until BOARD_DIM) {
            val square = Square(Row(rowIndex), Column(colIndex))
            if (square.black) {
                if (rowIndex in 0 until 3) {
                    mapa[square] = Piece.Black  // Atribui peça preta à casa
                }
                if (rowIndex in 3 .. 4) {
                    mapa[square] = Piece.Empty  // Atribui peça preta à casa
                }
                if (rowIndex in 5..BOARD_DIM) {
                    mapa[square] = Piece.White  // Atribui peça preta à casa
                }
            }
        }
    }
    return mapa
}

//TODO: Atualizar codigo do Galo Compose em relação ao codigo do git

//TODO: Para começar um novo jogo temos de edit mutiple instance e só recomeçar o jogo


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
    isKing(from, to)

    if (fromPiece == null) {
        println("Erro: Não há peça na posição de origem $from.")
        return false
    }

    if (toPiece == null) {
        println("Erro: A posição de destino $to já está ocupada.")
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
fun newBoard() : BoardRun{
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


fun BoardRun.isKing(from: Square, to: Square) {
    val fromPiece = moves[from]


    if ((to.row.index == 0 && fromPiece!!.color == 'w') ||
        (to.row.index == BOARD_DIM - 1 && fromPiece!!.color == 'b')
    ) {
        fromPiece.isQueen = true
    }
}


fun BoardRun.playMove(from: Square, to: Square) {
    val fromPiece = moves[from]

    // Move a peça para o destino e limpa a posição original
    moves[to] = fromPiece
    moves[from] = Piece.Empty  // Define como vazio mas disponivel

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
    // Determina a posição intermediária (onde a peça adversária está)

    val rowDifference = to.row.index - from.row.index
    val columnDifference = to.column.index - from.column.index

    val toRow = from.row.index + 2 * rowDifference
    val toCol = from.column.index + 2 * columnDifference

    val midRow = (from.row.index + toRow) / 2
    val midColumn = (from.column.index + toCol) / 2

    // Remove a peça adversária e substitui por '-'
    moves[Square(Row(midRow), Column(midColumn))] = Piece.Empty
    // Move a peça da origem para o destino
    moves[Square(Row(toRow), Column(toCol))] = moves[from]
    // Define a posição original com '-'
    moves[from] = Piece.Empty

}


