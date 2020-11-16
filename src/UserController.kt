import com.inno.music.User
import io.ktor.html.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserController {

    fun create(user: User): User {
        val userId = transaction {
            UserDao.insert {
                it[email] = user.email
                it[username] = user.username
                it[password] = user.password
            }[UserDao.id]
        }
        return user.copy(id = userId)
    }

    fun show(login: String?): User? {
        return if (login.isNullOrEmpty()) null else
            transaction {
            UserDao.select { UserDao.username eq login }
                .map { User(id = it[UserDao.id],
                    username = it[UserDao.username],
                    password = it[UserDao.password],
                    email = it[UserDao.email]) }
                .first()
        }
    }

    fun update(id: Int, newUser: User): User {
        transaction {
            UserDao.update({UserDao.id eq id}){
                it[username] = newUser.username
                it[email] = newUser.email
                it[password] = newUser.password
            }
        }
        return newUser.copy(id = id)
    }

}