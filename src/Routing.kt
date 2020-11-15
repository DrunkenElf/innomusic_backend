package com.inno.music

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*


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
        get("/get_user"){
            call.respondText("/get_user")
        }
        post("/login"){
            call.respondText("/login")
        }
    }
}

fun Route.audio(){
    route("audio"){

    }
}