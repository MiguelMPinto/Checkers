package isel.tds.Storage

import isel.tds.model.Board
import kotlin.io.path.*

class TextFileStorage(
    private val folderName: String,
    private val serializer: Serializer<Board>
) {
    private val basePath = Path(folderName)

    init {
        if (!basePath.exists()) basePath.createDirectory()
    }

    fun saveBoardToFile(board: Board, filename: String) {
        val file = basePath.resolve("$filename.txt")
        file.writeText(serializer.serialize(board))
    }

    fun loadBoardFromFile(filename: String): Board? {
        val file = basePath.resolve("$filename.txt")
        return if (file.exists()) serializer.deserialize(file.readText()) else null
    }
}
