package isel.tds.view


class Files {
    /*

    fun writeObjectToFile(obj: Serializable, fileName: String) {
        ObjectOutputStream(FileOutputStream(fileName)).use { outputStream ->
            outputStream.writeObject(obj)
        }
    }

    fun readObjectFromFile(fileName: String): Any {
        ObjectInputStream(FileInputStream(fileName)).use { inputStream ->
            return inputStream.readObject()
        }
    }

    fun init(input: List<String>) {
        if (input.size == 2) {
            filename = input[1]
            val filePath = "$filename.txt"
            if (File(filePath).exists()) {
                // Carregar o estado atual do tabuleiro do arquivo
                board = readObjectFromFile(filePath) as Board
                println("Estado do tabuleiro carregado do arquivo $filePath.")
            } else {
                // Configura o tabuleiro com o estado inicial e cria o arquivo
                board.setupPieces()
                writeObjectToFile(board, filePath)
                println("Arquivo $filePath criado com o estado inicial do tabuleiro.")
            }
        } else {
            println("Comando 'start' inválido. Use: start <nome_do_arquivo>")
        }
    }

    fun grid(file: String): Any {
        return readObjectFromFile("$file.txt")
    }
    */

}