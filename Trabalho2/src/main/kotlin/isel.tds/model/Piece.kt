package isel.tds.model

enum class Piece(var color: Char, var isQueen: Boolean = false){
    White('w'),
    Black('b'),
    Empty('-'),
    DamaWhite('W'),
    DamaBlack('B');

    val other get() = if(this == White) Black else White


    override fun toString(): String {
        return color.toString()  // Retorna a cor da peça
    }

}


fun String.toPieceOrNull(): Piece? {
    return Piece.entries.firstOrNull { it.name == this }
}

fun String.toPiece(): Piece {
    return when (this) {
        "w" -> Piece.White
        "b" -> Piece.Black
        "-" -> Piece.Empty // "-" para representar uma casa vazia
        "W" -> Piece.DamaWhite
        "B" -> Piece.DamaBlack
        else -> throw IllegalArgumentException("Invalid piece representation: $this")
    }
}