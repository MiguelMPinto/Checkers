package isel.tds.view

import isel.tds.model.*
import validateCommandArgs


class Command(
    val argsSyntax: String = "",
    val isToFinish: Boolean = false,
    val execute: (args: List<String>, clash: Clash,) -> Clash = { _, c:Clash -> c} //trailing function
)

//play 3a 4b
private val Play = Command() { args, clash ->

    require(clash is ClashRun) { "Game not started" }

    val (origin, destination) = validateCommandArgs(args)

    requireNotNull(args.firstOrNull()) {"Missing position"}

    clash.play(origin,destination)
}





private fun beginCommand(exec: Clash.(Name) -> Clash) =
    Command("<name>") { args, clash ->
        val word = requireNotNull(args.firstOrNull()) {"Missing name"}
        clash.exec(Name(word))
    }


fun getCommands(): Map<String, Command>  =
    mapOf(
        "PLAY" to Play,
        "EXIT" to Command(isToFinish = true),
        "START" to beginCommand { name -> this.startClash(name) },
        "JOIN" to beginCommand { name -> joinClash(name) },
        "REFRESH" to Command { _, clash -> clash.refresh() },
    )



