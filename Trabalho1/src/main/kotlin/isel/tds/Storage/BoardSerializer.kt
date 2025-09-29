package isel.tds.Storage

import isel.tds.model.*
import isel.tds.model.square.Square
import model.square.Column
import model.square.Row


object BoardSerializer : Serializer<Board> {

    override fun serialize(data: Board): String {
        val movesString = data.moves.entries.joinToString(" ") { (square, piece) ->
            "${square.row.index},${square.column.index}:${piece?.let {
                when (it) {
                    Piece.White -> "w"
                    Piece.Black -> "b"
                    Piece.Empty -> "-"
                }
            } ?: "null"}"
        }
        return when (data) {
            is BoardRun -> "run ${data.turn} | $movesString"
            is BoardWin -> "win ${data.winner} | $movesString"
            is BoardDraw -> "draw | $movesString"
        }
    }

    override fun deserialize(text: String): Board {
        val (left, right) = text.split(" | ")
        val moves = if (right.isBlank()) {
            mutableMapOf()
        } else {
            right.split(" ")
                .map { it.split(":") }
                .associate { (pos, player) ->
                    val (row, col) = pos.split(",").map { it.toInt() }
                    Square(Row(row), Column(col)) to when (player) {
                        "w" -> Piece.White
                        "b" -> Piece.Black
                        "-" -> Piece.Empty
                        "null" -> null
                        else -> throw IllegalArgumentException("Invalid piece: $player")
                    }
                }.toMutableMap()
        }


        val (type, player) = left.split(" ")

        return when (type) {
            "run" -> BoardRun(moves, player.toPiece())
            "win" -> BoardWin(moves, player.toPiece())
            "draw" -> BoardDraw(moves)
            else -> throw IllegalArgumentException("Invalid board type: $type")
        }
    }
}