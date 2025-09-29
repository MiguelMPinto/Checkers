package isel.tds.view

import isel.tds.Storage.TextFileStorage
import isel.tds.Storage.BoardSerializer
import isel.tds.model.Clash
import readCommand


object ConsoleApplication{
    fun start() {

        var clash = Clash(TextFileStorage("games", BoardSerializer))
        val commands = getCommands()

        while (true) {
            val (cmdName, args) = readCommand()
            val cmd = commands[cmdName]
            if (cmd == null) {
                println("Invalid command $cmdName")
            }

            else try {
                println(args)
                println(clash)
                clash = cmd.execute(args, clash)
                clash.show()
                if (cmd.isToFinish) break

            } catch (e: IllegalStateException) {
                println(e.message)
            } catch (e: IllegalArgumentException) {
                println("${e.message}\nUse: $cmdName")
            }catch(e: Exception){
                println("Error: ${e.message}")
            }
        }

    }
}





