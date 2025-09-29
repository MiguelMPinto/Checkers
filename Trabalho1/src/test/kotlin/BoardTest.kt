

import isel.tds.model.Board
import isel.tds.model.play
import isel.tds.view.Files
import isel.tds.model.square.toSquare
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class BoardTest {


    @Test
    fun `Test is valid move and isPlayerPieceOrIsPieceAt`() {
        val board = Board()
        board.setupPieces()

        // Movimenta uma peça branca de "2b" para "3c"
        val moved = play("2b".toSquare(), "3c".toSquare())
        assertEquals("Ainda é a sua vez de jogar", moved)


        //TODO: Por perceber ainda
        /*// Verifica se a peça está agora em "3c"
        val originSquare = "3c".toSquare()
        val isPlayerPiece = board.isPlayerPieceOrIsPieceAt(originSquare, GameState.WHITE_PLAYING)
        assertTrue(isPlayerPiece, "Piece at 3c should belong to the current player")

        // Verifica se "2b" está vazio
        val emptySquare = "2b".toSquare()
        val isPlayerPieceEmpty = board.isPlayerPieceOrIsPieceAt(emptySquare, GameState.WHITE_PLAYING)
        assertFalse(isPlayerPieceEmpty, "Square 2b should be empty after the move")*/
    }


    @Test
    fun `Chegar a salto duplo`(){ //TODO: Por testar
        val board = Board()
        val files = Files()

        files.init(parts)



        board.setupPieces()
        println(board.toString())


        var move = play("3g".toSquare(), "4h".toSquare())
        assertEquals("Next turn", move)

        move = play("6d".toSquare(), "5c".toSquare())
        assertEquals("Next turn", move)

        move = play("2h".toSquare(), "3g".toSquare())
        assertEquals("Next turn", move)

        move = play("7e".toSquare(), "6d".toSquare())
        assertEquals("Next turn", move)

        move = play("1g".toSquare(), "2h".toSquare())
        assertEquals("Next turn", move)

        move = play("5c".toSquare(), "4b".toSquare())
        assertEquals("Next turn", move)

        move = play("3a".toSquare(), "5c".toSquare())
        assertEquals("Ainda é a sua vez de jogar", move)

    }
}