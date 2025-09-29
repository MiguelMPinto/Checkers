package isel.tds

import AppViewModel
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import isel.tds.Storage.MongoDriver
import isel.tds.ui.*


@Composable
@Preview
private fun FrameWindowScope.GridApp(driver: MongoDriver, onExit: () -> Unit) {

    val scope = rememberCoroutineScope()
    val vm: AppViewModel = remember { AppViewModel(driver, scope) }


    MaterialTheme {
        MenuBar {
            Menu("Game") {
                Item("Start game", onClick = vm::openStartDialog)
                Item("Join game", onClick = vm::openJoinDialog)
                //Item("New board", onClick = vm::newBoard)
                //Item("Refresh", enabled = vm.hasClash, onClick = vm::refresh)
                Item("Exit", onClick = onExit)
            }
            Menu("Options") {
                Item("Auto Refresh", onClick = vm::autoRefresh)
                Item("Show Targets",enabled = vm.hasClash, onClick = vm::showTarget)
            }
        }
        Column() {
            // Coordenadas e Tabuleiro Alinhados
            GridView(vm.board?.moves, onClickCell = vm::play,vm.board, vm.sidePlayer, selectedSquare = vm.selectedSquare, gameName = vm.nameGame?:"No Game", validMoves = vm.validMoves)
        }

        //if (vm.viewScore) ScoreDialog(vm.score, vm.name,onClose = vm::hideScore)

        vm.inputName?.let {
            StartOrJoin(
                type = it,
                onCancel = vm::closeStartOrJoinDialog,
                onAction= if (it== InputName.ForStart) vm::start else vm::join
            ) }
        vm.errorMessage?.let { ErrorDialog(it, onClose = vm::hideError) }
        if (vm.isWaiting) waitingIndicator()
    }
}

fun main() = MongoDriver("Checkers").use { driver ->
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = WindowState(size = DpSize.Unspecified),
            title = "Checkers"
        ) {
            GridApp(driver, ::exitApplication)
        }
    }
}