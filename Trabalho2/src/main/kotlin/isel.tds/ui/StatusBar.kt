package isel.tds.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isel.tds.model.*


@Composable
fun StatusBar(board: Board?, you: Piece?, gameName: String) {

    Row(
        modifier = Modifier
            .width(GRID_WIDTH)
            .background(Color(0xFFA9682C)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Exibir o nome do jogo apenas se for BoardRun
        if (board is BoardRun) {
            Text(
                text = "Game: $gameName",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        // Exibir o jogador atual (You)
        you?.let {
            Text(
                text = "You ",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(end = 4.dp)
            )
            PlayerView(modifier = Modifier.padding(end = 16.dp), player = it)
        }

        // Exibir o turno, vencedor ou empate
        val (text, player) = when (board) {
            null -> "Game not started" to null
            is BoardRun -> "Turn: " to board.turn
            is BoardWin -> "Winner: ${board.winner}" to board.winner
            is BoardDraw -> "Draw" to null
        }

        Text(text, fontSize = 32.sp)
        player?.let {
            Spacer(Modifier.width(10.dp))
            PlayerView(32.dp, player)
        }
    }
}