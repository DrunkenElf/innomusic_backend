import com.inno.music.Download
import com.inno.music.Upload
import com.inno.music.audioRootFile
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.util.*
import io.ktor.util.url

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.css.form
import kotlinx.css.h2
import kotlinx.html.*
import java.io.File

fun Route.upload() {
    get<Upload> { upload ->
        call.respondHtml {
            head {
                title("Upload file")
            }

            body {
                h2 { +"Upload audio" }

                form(
                    "/audio/upload/${upload.type}",
                    classes = "pure-form-stacked",
                    encType = FormEncType.multipartFormData,
                    method = FormMethod.post,
                ) {
                    acceptCharset = "utf-8"

                    label {
                        htmlFor = "title"; +"Title:"
                        textInput { name = "title"; id = "title" }
                    }

                    br()
                    fileInput { name = "file" }
                    br()

                    submitInput(classes = "pure-button pure-button-primary") { value = "Upload" }
                }
            }
        }
    }

    post("audio/upload/post") {
        val multipart = call.receiveMultipart()
        val audioController = AudioController()

        var title = ""
        var audio = AudioFile()

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "title" -> audio.title = part.value
                        "type" -> audio.type = AudioType.SONG
                    }
                }
                is PartData.FileItem -> {
                    val out = File(audioRootFile, part.originalFileName ?: "empty filename.mp3")
                        .also { it.copyInputStreamToFile(part.streamProvider.invoke()) }//save to file
                    //title, type

                    audio = audio.copy(
                        path = out.path,
                    )
                }
                is PartData.BinaryItem -> {
                    println("binary item")
                }
            }
            part.dispose
        }
        audio.let {
            println("TITLE: ${audio.path}")
            val id = audioController.upload(audio)
            if (audio.path.isNotEmpty() && id != null)
                call.respondText { "Uploaded id: $id" }
            else
                call.respondText { "Error" }
        }
    }

}

fun Route.download() {
    get("/audio/download/{id}") {
        withContext(Dispatchers.IO) {
            val id = (call.parameters["id"] ?: "0").toInt()
            val audioController = AudioController()
            val audio = audioController.download(id)
            println("title: $id")
            call.respondFile(audioRootFile, audio.path, object : OutgoingContent.ByteArrayContent() {
                override val contentType: ContentType get() = ContentType.Audio.Any
                override val headers: Headers
                    get() = Headers.build {
                        append("id", id.toString())
                        append("title", audio.title)
                        append("type", audio.type.toString())
                    }

                override fun bytes(): ByteArray {
                    return File(audioRootFile, audio.path).readBytes()
                }

            })
        }
    }

    get("/audio/list") {
        withContext(Dispatchers.IO) {
            val audioController = AudioController().list()
            call.respondHtml {
                head {
                    title(audioController.size.toString())
                }
                body {
                    audioController.forEach {
                        ul {
                            li {
                                text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ApplicationCall.respondFile(baseDir: File, fileName: String, configure: OutgoingContent.ByteArrayContent) {

}

fun Route.deleteAll() {
    get("/audio/removeAll") {
        withContext(Dispatchers.IO) {
            val audioController = AudioController()
            audioController.removeAll()
            call.respondRedirect("/audio/upload")
        }
    }
}