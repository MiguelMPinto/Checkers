package isel.tds.model

import isel.tds.model.square.Square
import isel.tds.model.square.toSquareOrNull
import model.square.Column
import model.square.Row



fun BoardRun.nexTurn(){
    turn = if (turn == Piece.White) Piece.Black else Piece.White
}

fun BoardRun.rightTurn(from: Square): Boolean =
    turn != moves[from]

/*fun Board.possibleChoice(from: Square): List<Square> {//TODO: A verificar como ela deve se comportar com um mandatory eat
    val directions = listOfNotNull(
        try { Square(Row(from.row.index + 2), Column(from.column.index - 2)) } catch (e: IllegalArgumentException) { null },
        try { Square(Row(from.row.index - 2), Column(from.column.index - 2)) } catch (e: IllegalArgumentException) { null },
        try { Square(Row(from.row.index + 2), Column(from.column.index + 2)) } catch (e: IllegalArgumentException) { null },
        try { Square(Row(from.row.index - 2), Column(from.column.index + 2)) } catch (e: IllegalArgumentException) { null }
    )
    return directions.any { canEat(from, it) == true } //TODO: Entregar as posições onde dá certo numa lista
}*/


fun Board.hasPieces(color: Char): Boolean {//TODO: A mandar para o chat/Resolver
    /*for (row in pieces) {
        for (piece in row) {
            if (piece?.color == color) return true
        }
    }*/
    return true
}

fun BoardRun.mandatoryeat(from: Square): Boolean {
    val directions = listOfNotNull(
        try { Square(Row(from.row.index + 2), Column(from.column.index - 2)) } catch (e: IllegalArgumentException) { null },
        try { Square(Row(from.row.index - 2), Column(from.column.index - 2)) } catch (e: IllegalArgumentException) { null },
        try { Square(Row(from.row.index + 2), Column(from.column.index + 2)) } catch (e: IllegalArgumentException) { null },
        try { Square(Row(from.row.index - 2), Column(from.column.index + 2)) } catch (e: IllegalArgumentException) { null }
    )
    return directions.any { canEat(from, it) == true }
}

fun BoardRun.mandatoryto(from: Square, to: Square): Boolean{ //TODO: Verificar se passa testes
    eatPieces(from,to)
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


//TODO: Verificar a sequencia depois de comer o primeiro
fun Board.play(from: Square, to: Square): Board { //TODO: O right turn deveria avaliar o turn em função de outra coisa e não de que peças ele mexe
    val board = this

    if (this is BoardRun) {

        if (rightTurn(from) != false) {
            println( "Não é a tua vez de jogar")
            return board
        }

        val mandatory = iterateByColor(board, turn)
        val canEat = canEat(from, to)
        val mandatory_to = if (canEat == false) {
            false
        } else {
            mandatoryto(from, to)
        }

        if (board.moves[from]?.isQueen == true) {
            if (!canMoveAsQueen(from, to)) {
                println( "Movimento inválido para uma dama.")
                return board
            }
            if (mandatory != "false" && canEat == false) {
                println( "Tem de comer em $mandatory")
                return board
            }
        } else {

            if (mandatory != "false" && canEat == false) {
                println( "Tem de comer em $mandatory")
                return board
            } else if (mandatory_to == true && canEat == true) {
                eatPieces(from, to)
                println( "Ainda é a sua vez de jogar")
                return board
            } else if (mandatory == "false" &&
                canEat == false && nextStep(from, to) == true
            ) {
                playMove(from, to)
                nexTurn()
                println( "Next turn")
                return board
            } else if (mandatory == "false" && canEat == false
                && nextStep(from, to) == false
            ) {
                println( "Tens de fazer uma jogada valida")
            } else if (mandatory_to == false && canEat == true) {
                eatPieces(from, to)
                nexTurn()
                println( "Boa next turn")
                return board
            }

            println( "Invalid Comand")
            return board
        }
    }
    println( "Invalid Comand")
    val a = BoardRun(this.moves, (this as BoardRun).turn) //TODO: Fazer isto para tornar isto imutavel
    return a
}
