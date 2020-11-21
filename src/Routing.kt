package com.inno.music

import User
import UserController
import deleteAll
import download
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import upload

fun Application.routing(){
    routing {
        get("/asd"){
            call.respondText("Routing /")
        }
        route("/api"){
            user()
        }

        route("/audio"){
            upload()
            download()
            deleteAll()
        }


        get("/list"){
            println("parent: ${audioRootFile.name}")
            val temp = audioRootFile.listFiles()
            temp?.forEach {
                println("child: ${it.name}")
            }
        }

    }
}


fun Route.user() {
    route("/user"){
        val userController = UserController()

        get("/get_user/{username}"){
            val user = userController.show(call.parameters["username"])
            if (user != null)
                call.respond(user)
            else
                call.respond("No such user or empty param")
        }
        post("/login_post"){
            //val user = call.receiveParameters().entries().toString().parseUrlEncodedParameters()
            val user = withContext(Dispatchers.IO){ call.receive(User::class) }
            //println(user)
            call.respond(userController.create(user))
        }


    }
}



