import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserController {

    suspend fun create(user: User): User {

        val userId = transaction {
            SchemaUtils.create(Users)
            Users.insert {
                it[email] = user.email
                it[username] = user.username
                it[password] = user.password
            } get Users.id
        }
        return user.copy(id = userId)
    }

    fun show(login: String?): User? {
        return if (login.isNullOrEmpty()) null else
            transaction {
            Users.select { Users.username eq login }
                .map {
                    User(
                        id = it[Users.id],
                        username = it[Users.username],
                        password = it[Users.password],
                        email = it[Users.email]
                    )
                }
                .first()
        }
    }

    fun update(id: Int, newUser: User): User {
        transaction {
            Users.update({Users.id eq id}){
                it[username] = newUser.username
                it[email] = newUser.email
                it[password] = newUser.password
            }
        }
        return newUser.copy(id = id)
    }

}