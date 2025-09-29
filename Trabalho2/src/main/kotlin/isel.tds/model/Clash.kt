package isel.tds.model

import isel.tds.Storage.MongoStorage
import isel.tds.model.square.Square

typealias GameStorage = MongoStorage<Name, Board>

open class Clash(val gs: MongoStorage<Name, Board>)

class ClashRun(
    gs: GameStorage,
    val id: Name,
    val sidePlayer: Piece ,
    val board: Board
) : Clash(gs)

// Ao tentar criar uma segunda vez partida com o mesmo nome dá delete da primeira
fun Clash.deleteIfIsOwner() {
    if (this is ClashRun && sidePlayer==Piece.White) gs.delete(id)
}

// Cria uma ficheiro de jogo se não for novo apaga o anterior
fun Clash.startClash(name: Name): Clash {
    val board = newBoard()
    gs.create(name, board)
    deleteIfIsOwner()
    return ClashRun(gs, name, Piece.White, board).newBoardd()
}

// Verifica que existe um jogo ativo com aquele nome
private fun Clash.runOper(actions: ClashRun.()->Board): Clash {
    check(this is ClashRun) { "Clash not started" }
    return ClashRun(gs, id, sidePlayer, actions())
}

// Apenas reinicia o tabuleiro
fun Clash.newBoardd() = runOper {
    newBoard().also { gs.update(id,it) }
}


// Permite que o 'O' faça jogadas no mesmo ficheiro
fun Clash.joinClash(name: Name): Clash {
    val game = gs.read(name) ?: error("Clash $name not found")
    deleteIfIsOwner()
    return ClashRun(gs, name, Piece.Black, game)
}

// Verifica se pode jogar e se está na sua vez de jogar se sim altera o board
fun Clash.play(from: Square, to: Square) = runOper {
    val gameAfter = board.play(from, to)
    gameAfter.also { gs.update(id,it) }
}

// Se houve alguma mudança apresenta o novo board
fun Clash.refresh() = runOper {
    val gameAfter = gs.read(id) as Board
    check(board!=gameAfter) { "No changes" }
    gameAfter
}

class NoChangesException : IllegalStateException("No changes")
class GameDeletedException : IllegalStateException("Game deleted")