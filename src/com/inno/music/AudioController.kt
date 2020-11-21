package com.inno.music

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AudioController {

    suspend fun upload(audio: AudioFile): Int {
        return transaction {
            //SchemaUtils.drop(com.inno.music.Audios)
            SchemaUtils.create(Audios)
            Audios.insert {
                it[title] = audio.title
                it[type] = audio.type.name
                it[path] = audio.path
            } get Audios.id
        }
    }

    suspend fun download(id: Int): AudioFile {
        return transaction {
            Audios.select { Audios.id eq id }
                .map {
                    AudioFile(
                        id = it[Audios.id],
                        title = it[Audios.title],
                        type = AudioType.SONG,
                        path = it[Audios.path],
                    ) }.first()
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