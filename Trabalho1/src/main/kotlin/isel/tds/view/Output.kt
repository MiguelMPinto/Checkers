package isel.tds.view


import isel.tds.Storage.BoardSerializer
import isel.tds.model.*
import isel.tds.model.square.Square
import model.square.Column
import model.square.Row
import java.io.File

var filename = ""


fun Board.printBoard() {
    println(" +-----------------+  ") // Exibe o turno do jogador atual

    for (rowIndex in 0 until BOARD_DIM) {
        val row = Row(rowIndex) // Usa a classe Row para representar as linhas
        print("${row.digit}| ") // Imprime o número da linha

        for (colIndex in 0 until BOARD_DIM) {
            val column = Column(colIndex) // Usa a classe Column para representar as colunas
            val square = Square(row, column)
            if (moves[square] != null) {
                print(moves[square]!!.symbol()) // Imprime a peça
            } else {
                if (row.index in 3..4 && (square.black)) {
                    moves[square] = Piece.Empty
                    print("-") // Linhas 4 e 5 com '-'
                } else {
                    print(" ") // Outras casas vazias
                }
            }
            print(" ")
        }
        println("|") // Fecha a linha do tabuleiro sem o Player

    }
    println(" +-----------------+")
    print("  ")
    for (colIndex in 0 until BOARD_DIM) {
        val column = Column(colIndex)
        print(" ${column.symbol}") // Imprime a letra da coluna
    }
    println() // Quebra de linha final
    println( when(this) {
        is BoardDraw -> "Draw"
        is BoardRun -> "turn: $turn"
        is BoardWin -> "winner: $winner"
    })
}

fun Clash.show() {
    if (this is ClashRun) {
        println("Clash: $id Player: $sidePlayer")
        board.printBoard()
    }
    else println("Clash not started")
}

// Serializa o estado atual do tabuleiro em um arquivo
fun saveBoardState(board: Board, filename: String) {

    val serializedBoard = BoardSerializer.serialize(board)
    File(filename).writeText(serializedBoard)

}

// Desserializa e exibe o estado do tabuleiro a partir do arquivo

fun loadAndDisplayBoard(file_name: String):Board? {
    try {

        val file = File(file_name)
        if (!file.exists()) {
            throw IllegalArgumentException("Arquivo '$filename' não encontrado.")
        }

        val serializedBoard = file.readText()

        // Regex para capturar o turno do jogador ('w' ou 'b')
        val playerTurnRegex = """\brun\s([wb])\b""".toRegex()
        val playerTurnMatch = playerTurnRegex.find(serializedBoard)
        val playerTurn = when (playerTurnMatch?.groupValues?.get(1)) {
            "w" -> Piece.White
            "b" -> Piece.Black
            else -> throw IllegalArgumentException("Erro: Não foi possível determinar o turno do jogador.")
        }

        val board = BoardSerializer.deserialize(serializedBoard)


        board.printBoard()
        return board

    } catch (e: Exception) {
        println("Erro ao carregar o tabuleirooo: ${e.message}")
    }
    return null
}


fun load(filename: String ): String{

    val serializedBoard = File(filename).readText()

    return serializedBoard
}

fun initt(commandArgs: List<String>) : Board?{

    filename = commandArgs[1]

    val filePath = "$filename.txt"

    if (File(filePath).exists()) {
        val actual_board = loadAndDisplayBoard(filePath)
        println("Estado do tabuleiro carregado do arquivo $filePath.")
        return actual_board
    }
    else {
        val newBoard = newBoard()
        val file = File(filePath)

        saveBoardState( newBoard, file.toString()) // Serializa após a jogada
        loadAndDisplayBoard(file.toString())
        return newBoard
    }
}


fun grid(file: String): Board? {
    return loadAndDisplayBoard("$file.txt")
}