import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AudioController {

    suspend fun upload(audio: AudioFile): Int{
        return transaction {
            SchemaUtils.create(Audios)
            Audios.insert {
                it[title] = audio.title
                it[type] = audio.type.name
                it[data] = audio.data!!
                it[path] = audio.path
            } get Audios.id
        }
    }

    suspend fun download(title: String): AudioFile {
        return transaction {
            Audios.select{ Audios.title eq title}
                .map { AudioFile(id = it[Audios.id],
                    title = it[Audios.title],
                    type = AudioType.SONG,
                    path = it[Audios.path],
                    data = it[Audios.data],)
                }.first()
        }
    }

    fun list(): List<String> {
        return transaction {
            Audios.selectAll().map { it[Audios.title] }
        }
    }

    fun removeAll() {
        transaction {
            Audios.deleteAll()
        }
    }
}