package com.inno.music

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.http.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.locations.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.server.engine.*
import io.ktor.gson.*
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.time.Duration

fun initDB(){
    /*val config = HikariConfig("/hikari_web.properties")
    val ds = HikariDataSource(config)
    Database.connect(ds)*/
    val hikariConfig = HikariConfig()
    val DATABASE_URL = System.getenv("DATABASE_URL")
    val credentialsAndConnectionString = DATABASE_URL.split("@")
    val credentials = credentialsAndConnectionString[0].split("postgres://")[1].split(":")
    val connectionString = credentialsAndConnectionString[1]
    hikariConfig.jdbcUrl = "jdbc:postgresql://$connectionString"
    hikariConfig.driverClassName = "org.postgresql.Driver"
    hikariConfig.username = credentials[0]
    hikariConfig.password = credentials[1]

    val ds = HikariDataSource(hikariConfig)
    Database.connect(ds)
}

val audioRootFile = File("resources/audios").also { if (!it.exists()) it.mkdirs()}

fun Application.main() {
    install(CORS){
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
    install(Locations) {
    }
    install(Authentication){}

    install(ForwardedHeaderSupport)

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(AutoHeadResponse)

    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DataConversion){
        /*convert<User> {
            
            decode { values, type ->
            }
        }*/
    }


    install(ShutDownUrl.ApplicationCallFeature) {
        // The URL that will be intercepted (you can also use the application.conf's ktor.deployment.shutdown.url key)
        shutDownUrl = "/ktor/application/shutdown"
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(StatusPages) {
        exception<AuthenticationException> { cause ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { cause ->
            call.respond(HttpStatusCode.Forbidden)
        }

    }
    initDB()

    intercept(ApplicationCallPipeline.Call){
        println("intercept call: request uri = "+call.request.uri)
        println("intercept call: request path = "+call.request.path())
        println("intercept call: request method = "+call.request.httpMethod)
        println("intercept call: response = "+call.response)
        println("intercept call: parameters = "+call.parameters.entries())
        println("intercept call: attributes = "+call.attributes)
        println("intercept call: callId = "+call.callId)


    }


 /*   routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }

        get<MyLocation> {
            call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
        }
        // Register nested routes
        get<Type.Edit> {
            call.respondText("Inside $it")
        }
        get<Type.List> {
            call.respondText("Inside $it")
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }

        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }*/
}

@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@Location("/type/{name}") data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

suspend fun ApplicationCall.respondRedirect(location: Any) = respondRedirect(url(location), permanent = false)
