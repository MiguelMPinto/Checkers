import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import isel.tds.model.*
import isel.tds.Storage.BoardSerializer
import isel.tds.Storage.MongoDriver
import isel.tds.Storage.MongoStorage
import isel.tds.ui.InputName
import kotlinx.coroutines.*
import isel.tds.model.square.Square


class AppViewModel(driver: MongoDriver, val scope: CoroutineScope) {
    private val storage =
        MongoStorage<Name, Board>("tds", driver, BoardSerializer)

    var clash by mutableStateOf(Clash(storage))   // Model state
    var inputName by mutableStateOf<InputName?>(null) //StartOrJoinDialog
        private set
    var errorMessage by mutableStateOf<String?>(null) //ErrorDialog state
        private set
    var waitingJob by mutableStateOf<Job?>(null)
    var autoRefresh by mutableStateOf(true)
    var showTarget by mutableStateOf(false)
    var selectedSquare by mutableStateOf<Square?>(null) // Quadrado selecionado
    var nameGame by mutableStateOf<String?>(null) // Quadrado selecionado
    val isWaiting: Boolean get() = waitingJob != null
    var validMoves by mutableStateOf<List<Square>>(emptyList())   //NOVO

    private val turnAvailable: Boolean
        get() = (board as? BoardRun)?.turn == sidePlayer

    val board: Board? get() = (clash as? ClashRun)?.board
    val hasClash: Boolean get() = clash is ClashRun
    val sidePlayer: Piece? get() = (clash as? ClashRun)?.sidePlayer

    fun hideError() {
        errorMessage = null
    }

    private fun exec(fx: Clash.() -> Clash): Unit =
        try {
            clash = clash.fx()
        } catch (e: Exception) {        // Report exceptions in ErrorDialog
            errorMessage = e.message
        }


    fun play(from: Square) {
        if (board !is BoardRun) {
            errorMessage = "ERROR: Game Not Started"
            return
        }
        if (selectedSquare == null) {
            selectedSquare = from // Define o quadrado selecionado
            if (showTarget) {
                validMoves = ((board as BoardRun).calculateValidMoves(selectedSquare!!))
            }
        } else {
            exec { play(selectedSquare!!, from) }
            validMoves = emptyList()
            selectedSquare = null // Reseta o quadrado selecionado após o movimento
            if (autoRefresh) {
                waitForOtherSide()
            }
        }
    }


    private fun waitForOtherSide() {
        if (turnAvailable) return
        waitingJob = scope.launch(Dispatchers.IO) {
            do {
                delay(5000)
                try {
                    clash = clash.refresh()
                } catch (e: NoChangesException) { /* Ignore */
                } catch (e: Exception) {
                    errorMessage = e.message
                    if (e is GameDeletedException) clash = Clash(storage)
                }
            } while (!turnAvailable)
            waitingJob = null
        }
    }

    fun autoRefresh() {
        autoRefresh = !autoRefresh
    }

    fun showTarget() {
        showTarget = !showTarget
    }

    fun refresh() = exec(Clash::refresh)

    fun openStartDialog() {
        inputName = InputName.ForStart
    }

    fun openJoinDialog() {
        inputName = InputName.ForJoin
    }

    fun closeStartOrJoinDialog() {
        inputName = null
    }

    fun start(name: Name) {
        nameGame = name.value
        closeStartOrJoinDialog()
        exec { startClash(name) }
        waitForOtherSide()
    }

    fun join(name: Name) {
        nameGame = name.value
        closeStartOrJoinDialog()
        exec { joinClash(name) }
        waitForOtherSide()
    }
}