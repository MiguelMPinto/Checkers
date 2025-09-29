import isel.tds.model.square.Square
import isel.tds.model.square.toSquareOrNull
import kotlin.system.exitProcess


data class CommandLine(val name: String, val args: List<String>)

tailrec fun readCommand(): CommandLine {
    print("> ")
    val line = readln().split(' ').filter { it.isNotBlank() }
    return if (line.isEmpty()) readCommand()
    else CommandLine(line.first().uppercase(), line.drop(1))
}

fun commandRight(parts:List<String>):Boolean{
    try {
        if ( (parts[0].length == 2 || parts[1].length == 2)) {
            return true
        }

    }
    catch (e:IllegalArgumentException){
        println("COMANDO INVÁLIDO! Deve ser no formato: <comando> <origem> <destino>")
        return false
    }
    catch (e:IndexOutOfBoundsException){
        println("COMANDO INVÁLIDO! Deve ser no formato: <comando> <origem> <destino>")
        return false
    }
    return false
}

fun validateCommandsName(commandArgs: String):Boolean{
    return when {
        commandArgs == "GRID" -> {

            true // Indica que o comando foi processado
        }
        commandArgs == "EXIT" -> {
            exitProcess(0) // Sai do programa
        }
        commandArgs == "REFRESH" -> {

            true
        }
        else -> false // Indica que o comando não foi reconhecido
    }
}

// Função auxiliar para validar o comando
fun validateCommandArgs(commandArgs: List<String>): Pair<Square, Square> {

    if (!commandRight(commandArgs)) throw IllegalArgumentException("ENTRADA INVÁLIDA! Deve ser no formato: <comando> <origem> <destino>")

    val origin = commandArgs[0].toSquareOrNull()
    val destination = commandArgs[1].toSquareOrNull()

    if (origin == null || destination == null) throw IllegalArgumentException("COORDENADAS INVÁLIDAS")



    return origin to destination

}


fun String.isPlay():Boolean {
    try{
        if (this == "PLAY") return true
    }
    catch (e: IllegalArgumentException){
        //Ignora
    }
    return false

}



fun String.isExit(): Boolean {

    if (this == "EXIT" ){
        println("jogo encerrado")
        return true
    }

    return false
}
