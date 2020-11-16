import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object AudioDao: Table(){
    val id: Column<Int> = integer("id").uniqueIndex()
    val title = varchar("title", 100)
    val type = varchar("type", 20)
    val path = varchar("path", 100)
    val data = binary("data")
}

object UserDao: Table(){
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id, username)
    val id = integer("id").autoIncrement()
    val username: Column<String> = varchar("username", 100 )
    val password = varchar("password", 40)
    val email = varchar("email", 100)
}
object PlaylistDao: Table(){
    val id: Column<Int> = integer("id").uniqueIndex()
    val title = varchar("title", 100)
    val releaseDate = long("releaseDate")
    val ownerId = integer("ownerId")
}
