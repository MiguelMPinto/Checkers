package isel.tds.model

import isel.tds.Storage.Storage
import isel.tds.model.square.Square

typealias GameStorage = Storage<Name, Board>

open class Clash( val gs: GameStorage)

class ClashRun(
    gs: GameStorage,
    val id: String,
    val sidePlayer: Piece ,
    val board: Board
) : Clash(gs)

// Ao tentar criar uma segunda vez partida com o mesmo nome dá delete da primeira
fun Clash.deleteIfIsOwner() {
    if (this is ClashRun && sidePlayer == Piece.White) {
        gs.delete(id.toName())
        println("Game '$id' deleted because player W is the owner.")
    }
}

// Cria uma ficheiro de jogo se não for novo apaga o anterior
fun Clash.startClash(name: Name): Clash {
    val board = newBoard()
    gs.create(name, board)
    deleteIfIsOwner()
    return ClashRun(gs, name.toString(), Piece.White, board).newBoardClash()
}

// Verifica que existe um jogo ativo com aquele nome
private fun Clash.runOper(actions: ClashRun.() -> Board): Clash {
    check(this is ClashRun) { "Clash not started" }
    val newBoard = actions()
    gs.update(id.toName(), newBoard) // Atualiza o tabuleiro no armazenamento
    return ClashRun(gs, id, sidePlayer, newBoard)
}

// Apenas reinicia o tabuleiro
fun Clash.newBoardClash() = runOper {
    newBoard().also { gs.update(id.toName(),it) }
}


// Permite que o 'O' faça jogadas no mesmo ficheiro
fun Clash.joinClash(name: Name): Clash {
    val game = gs.read(name) ?: error("Clash $name not found")
    deleteIfIsOwner()
    return ClashRun(gs, name.toString(), Piece.Black, game)
}

// Verifica se pode jogar e se está na sua vez de jogar se sim altera o board
fun Clash.play(from: Square, to: Square) = runOper {
    val gameAfter = board.play(from, to)
    gameAfter.also { gs.update(id.toName(),it) }
}

// Se houve alguma mudança apresenta o novo board
fun Clash.refresh() = runOper {
    val gameAfter = gs.read(id.toName()) as Board
    check(board!=gameAfter) { "No changes" }
    gameAfter
}