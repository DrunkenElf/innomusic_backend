import com.inno.music.audioRootFile
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.routing.get
import io.ktor.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import java.io.File

val domain = "http://innomusic.herokuapp.com/audio/"

fun Route.upload() {
    get("upload") {
        call.respondHtml {
            head {
                title("Upload file")
            }
            body {
                h2 { +"Upload audio" }
                form(
                    "upload",
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

    post("upload") {
        val multipart = call.receiveMultipart()
        val audioController = AudioController()

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
                        .also {
                            withContext(Dispatchers.IO){
                                if (!it.exists())
                                    it.createNewFile()
                            }
                            it.copyInputStreamToFile(part.streamProvider.invoke())
                        }
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
            if (audio.path.isNotEmpty())
                call.respondText { "Uploaded id: $id" }
            else
                call.respondText { "Error" }
        }
    }

}

fun Route.download() {
    get("download/{id}") {
        val id = (call.parameters["id"] ?: "0").toInt()
        val audioController = AudioController()
        val audio = audioController.download(id)
        println("title: $id")
        println("path: ${audio.path}")
        call.response.apply {
            header("id", id.toString())
            header("title", audio.title)
            header("type", audio.type.toString())
            header(HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment
                    .withParameter(ContentDisposition.Parameters.FileName,
                        domain+"direct/${id}").toString())
        }.call.respond(audio.copy(path = domain+"direct/${id}"))

    }

    get("direct/{id}"){
        withContext(Dispatchers.IO){
            val id = (call.parameters["id"] ?: "0").toInt()
            val audioController = AudioController()
            val audio = audioController.download(id)
            println("dir id: $id")
            println("dir path: ${audio.path}")
            val temp = File(audio.path)
            println("dir path is exis: ${temp.exists()}")
            call.respondBytes(temp.inputStream().readBytes(), ContentType.Audio.Any)
        }
    }

    get("list") {
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



fun Route.deleteAll() {
    get("removeAll") {
        withContext(Dispatchers.IO) {
            val audioController = AudioController()
            audioController.removeAll()
            call.respondRedirect("upload")
        }
    }
}