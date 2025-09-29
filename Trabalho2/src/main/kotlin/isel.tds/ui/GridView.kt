package isel.tds.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isel.tds.model.*
import isel.tds.model.square.Square
import model.square.Column
import model.square.Row

const val WOOD = 0xFFA9682C
const val SOFT_GREY = 0xFFC8C8C8
const val GREY = 0xFF414141
const val GREEN = 0XFF468039

val CELL_SIZE = 80.dp
val LINE_WIDTH = 5.dp
val GRID_WIDTH = CELL_SIZE * BOARD_DIM + LINE_WIDTH * (BOARD_DIM - 1)


@Composable
fun GridView(moves: Moves?, onClickCell: (Square) -> Unit, board: Board?, you: Piece?, selectedSquare: Square?, gameName: String, validMoves: List<Square>) {
    Column(
        modifier = Modifier
            .size(GRID_WIDTH + CELL_SIZE) // Remove espaço extra lateral
            .background(Color(WOOD)) // Fundo de madeira
    ) {
        // Linha com as letras das colunas (a-h) na margem superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CELL_SIZE / 2) // Altura ajustada para alinhar com as células
                .background(Color(WOOD)), // Fundo de madeira
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(BOARD_DIM) { col ->
                Box(
                    modifier = Modifier
                        .width(CELL_SIZE + 10.dp) // Largura igual ao tamanho de uma célula
                        .height(CELL_SIZE / 2), // Altura reduzida para alinhar as letras
                    contentAlignment = Alignment.Center // Centraliza cada letra no box
                ) {
                    Text(
                        text = ('a' + col).toString(), // Letras a-h
                        color = Color.Black,
                        fontSize = 24.sp
                    )
                }
            }
        }
        // Tabuleiro e números na lateral esquerda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start // Alinha ao início
        ) {
            // Coluna com os números das linhas (1-8) na lateral esquerda
            Column(
                modifier = Modifier
                    .height(GRID_WIDTH) // Altura igual ao tabuleiro
                    .width(CELL_SIZE - 40.dp), // Reduzi ainda mais a largura da borda lateral
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                repeat(BOARD_DIM) { row ->
                    Box(
                        modifier = Modifier
                            .size(CELL_SIZE),
                        contentAlignment = Alignment.Center // Centraliza os números
                    ) {
                        Text(
                            text = (BOARD_DIM - row).toString(), // Números 8 até 1
                            color = Color.Black, // Alterado para preto
                            fontSize = 24.sp
                        )
                    }
                }
            }
            // Tabuleiro interno com quadrados pretos e brancos
            Column(
                modifier = Modifier
                    .size(GRID_WIDTH)
                    .background(Color(GREY)), // Fundo preto para separar o tabuleiro
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(BOARD_DIM) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(BOARD_DIM) { col ->
                            val square = Square(Row(row), Column(col))
                            val isDarkCell = (row + col) % 2 == 1 // Células alternadas (escuro e claro)
                            val cellColor = if (isDarkCell) Color(GREY) else Color(SOFT_GREY)

                            // Verifica se o quadrado é o selecionado
                            val isSelected = selectedSquare == square
                            val backgroundColor = if (isSelected) Color.Red else Color.Black
                            val gross = if (isSelected) 4 else 1

                            Box(
                                modifier = Modifier
                                    .size(CELL_SIZE) // Define o tamanho da quadrícula
                                    .background(cellColor) // Cor do fundo (preto ou cinza)
                                    .clickable { onClickCell(square) },
                                contentAlignment = Alignment.Center // Garante que o conteúdo (bola verde) fica no centro
                            ) {
                                PlayerView(
                                    100.dp,
                                    moves?.get(square),
                                    onClick = { onClickCell(square) },
                                    modifier = Modifier
                                        .size(CELL_SIZE)
                                        .background(cellColor)
                                        .border(gross.dp, backgroundColor) // Para manter o design
                                )

                                if (square in validMoves) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(Color(GREEN), shape = CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Barra de status
        StatusBar(board, you, gameName)
    }
}


