package isel.tds.Storage

/*
import isel.tds.model
import isel.tds.model.Piece


object GameSerializer : Serializer<Game>{
    override fun serialize(data: Game) = buildString {
        appendLine( data.firstPlayer )
        data.board?.let { appendLine(BoardSerializer.serialize(it)) }
    }

    override fun deserialize(text: String) =
        text.split("\n").let{ (score,player,board) -> Game(
            firstPlayer = Piece.valueOf(player),
            board = if (board.isBlank()) null
            else BoardSerializer.deserialize(board)
        ) }

}
*/