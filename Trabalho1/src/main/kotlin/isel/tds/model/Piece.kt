package isel.tds.model

enum class Piece(var color: Char, var isQueen: Boolean = false){
    White('w'),
    Black('b'),
    Empty('-');

    fun symbol(): Char {
        return when {
            color == '-' -> '-'  // Traço para indicar casa vazia após a jogada
            isQueen && color == 'w' -> 'W'  // Dama branca
            isQueen && color == 'b' -> 'B'  // Dama preta
            color == 'w' -> 'w'
            else -> 'b'
        }
    }

    override fun toString(): String {
        return color.toString()  // Retorna a cor da peça
    }

}
fun String.toPiece(): Piece {
    return when (this) {
        "w" -> Piece.White
        "b" -> Piece.Black
        "-" -> Piece.Empty // Se você estiver usando "null" para representar uma casa vazia
        else -> throw IllegalArgumentException("Invalid piece representation: $this")
    }
}


