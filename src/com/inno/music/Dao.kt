package com.inno.music

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Audios: Table(){
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, title)
    val id = integer("id").autoIncrement()
    val title = varchar("title", 100).uniqueIndex()
    val type = varchar("type", 20)
    val path = varchar("path", 100)
}

object Users: Table(){
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, username)
    val id = integer("id").autoIncrement()
    val username = varchar("username", 100 )
    val password = varchar("password", 40)
    val email = varchar("email", 100)
}
object PlaylistDao: Table(){
    val id: Column<Int> = integer("id").uniqueIndex()
    val title = varchar("title", 100)
    val releaseDate = long("releaseDate")
    val ownerId = integer("ownerId")
}
