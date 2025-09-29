package isel.tds.model

import isel.tds.model.square.Square
import isel.tds.model.square.toSquareOrNull
import model.square.Column
import model.square.Row


fun BoardRun.nexTurn() {
    turn = if (turn == Piece.White) Piece.Black else Piece.White
}

fun BoardRun.rightTurn(from: Square): Boolean {
    if (this.moves[from]?.color == 'W' && this.turn.color == 'w' ){
        return true
    }
    if (this.moves[from]?.color == 'B' && this.turn.color == 'b' ){
        return true
    }
    return this.turn.color == this.moves[from]?.color
}

fun BoardRun.calculateValidMoves(from: Square): List<Square> {
    val directions = listOf(
        Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1) // Diagonais
    )
    val validMoves = mutableListOf<Square>()
    val captureMoves = mutableListOf<Square>()
    val fromPiece = moves[from] ?: return emptyList() // Verifica se há uma peça na origem
    // Determina as direções permitidas para movimentos simples com base na peça
    val allowedDirections = when (fromPiece) {
        Piece.White -> directions.filter { it.first < 0 } // Apenas "para cima"
        Piece.Black -> directions.filter { it.first > 0 } // Apenas "para baixo"
        Piece.DamaWhite, Piece.DamaBlack -> directions // Rainhas podem ir em todas as direções
        else -> return emptyList() // Casas vazias ou inválidas não têm movimentos
    }
    for ((rowOffset, colOffset) in allowedDirections) {
        try {
            // Movimentos simples
            val target = Square(
                Row(from.row.index + rowOffset),
                Column(from.column.index + colOffset)
            )
            if (nextStep(from, target)) {
                validMoves.add(target)
            }
            // Movimentos de captura
            val jumpTarget = Square(
                Row(from.row.index + 2 * rowOffset),
                Column(from.column.index + 2 * colOffset)
            )
            if (canEat(from, jumpTarget)) {
                captureMoves.add(jumpTarget)
            }
        } catch (e: IllegalArgumentException) {
            // Ignorar movimentos fora do tabuleiro
        }
    }

    // Se existirem movimentos de captura, retorná-los com prioridade
    return if (captureMoves.isNotEmpty()) captureMoves else validMoves
}

fun BoardRun.mandatoryeat(from: Square): Boolean {
    val directions = listOfNotNull(
        try {
            Square(Row(from.row.index + 2), Column(from.column.index - 2))
        } catch (e: IllegalArgumentException) {
            null
        },
        try {
            Square(Row(from.row.index - 2), Column(from.column.index - 2))
        } catch (e: IllegalArgumentException) {
            null
        },
        try {
            Square(Row(from.row.index + 2), Column(from.column.index + 2))
        } catch (e: IllegalArgumentException) {
            null
        },
        try {
            Square(Row(from.row.index - 2), Column(from.column.index + 2))
        } catch (e: IllegalArgumentException) {
            null
        }
    )
    return directions.any { canEat(from, it) }
}

fun BoardRun.mandatoryTo(from: Square, to: Square): Boolean {
    eatPieces(from, to)
    val ret = mandatoryeat(to)
    undomove(from, to)
    return ret
}

fun BoardRun.iterateByColor(board: Board, piece: Piece): String {
    for (row in 0 until BOARD_DIM) {
        for (col in 0 until BOARD_DIM) {
            val pieces = board.moves[Square(Row(row), Column(col))]
            if (pieces == piece) {
                val rowNumber = BOARD_DIM - row
                val colLetter = 'a' + col
                val from = (rowNumber.toString() + colLetter.toString()).toSquareOrNull()

                if (from != null && mandatoryeat(from)) {
                    return from.toString()
                }

            }
        }
    }
    return "false"
}

fun Board.play(from: Square, to: Square): Board {
    val board = this
    if (this is BoardRun) {

        check(rightTurn(from)) { "Não é a tua vez de jogar" }

        val mandatory = iterateByColor(board, turn)
        val canEat = canEat(from, to)
        val mandatoryTo = if (!canEat) false else mandatoryTo(from, to)

        if (board.moves[from] == Piece.DamaWhite || board.moves[from] == Piece.DamaBlack) {
            if (canMoveAsQueen(from, to)){
                playMove(from,to)
            }
            println("Movimento inválido para uma dama.")
        } else {
            check(!(mandatory != "false" && !canEat)) { "Tem de comer em $mandatory" }
            if (mandatoryTo && canEat) {
                eatPieces(from, to)
                // Chama isQueen após comer
                isQueen(from, to)
                println("Ainda é a sua vez de jogar")
                return board
            } else if (mandatory == "false" && !canEat && nextStep(from, to)) {
                playMove(from, to)
                // Chama isQueen após movimento normal
                isQueen(from, to)
                nexTurn()
                println("Next turn")
                return board
            }
            check(!(mandatory == "false" && !canEat && !nextStep(from, to))) { "Tens de fazer uma jogada válida" }
            if (!mandatoryTo && canEat) {
                eatPieces(from, to)
                // Chama isQueen após comer
                isQueen(from, to)
                nexTurn()
                println("Boa next turn")
                return board
            }
        }
    }
    println("Invalid Command")
    return BoardRun(this.moves, (this as BoardRun).turn)
}
