package com.inno.music

import UserController
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.FormEncType
import kotlinx.html.body
import kotlinx.html.form
import kotlinx.html.*
import kotlinx.html.stream.createHTML


fun Application.routing(){
    routing {
        get("/"){
            call.respondText("Routing /")
        }
        route("/api"){
            user()
            audio()
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
            val user = call.receiveParameters()

            call.respond(user.entries())
        }


    }
}

fun Route.audio(){
    route("audio"){

    }
}

