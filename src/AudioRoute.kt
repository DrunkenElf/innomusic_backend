import com.inno.music.Download
import com.inno.music.Upload
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

fun Route.upload(){
        get<Upload> { upload ->
            call.respondHtml {
                head {
                    title("Upload file")
                }

                body{
                    h2 { +"Upload video" }

                    form(
                        call.url(Upload() ),
                        classes = "pure-form-stacked",
                        encType = FormEncType.multipartFormData,
                        method = FormMethod.post
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

        post<Upload>{

                val multipart = call.receiveMultipart()
                val audioController = AudioController()

                var title = ""
                var audio: AudioFile? = AudioFile()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                          /*  if (part.name == "title")
                                audio?.copy(title = part.value)*/
                        }
                        is PartData.FileItem -> {
                            val ext = File(part.originalFileName).extension.also { println("ext: $it") }
                            println("origfilename: "+part.originalFileName)
                            async(Dispatchers.IO) {
                                audio = audio?.copy(
                                    title = part.originalFileName.toString(),
                                    data = part.streamProvider.invoke().readBytes()
                                )
                            }.await()
                        }
                        is PartData.BinaryItem -> {
                            println("binary item")
                        }
                    }
                    part.dispose
                }
                audio?.let {
                    println("TITLE: ${audio?.title}")
                    audioController.upload(audio!!)
                }
        }
}
fun Route.download(){
    get<Download> {
        withContext(Dispatchers.IO){
            val audioController = AudioController()
            val audio = audioController.download(it.title)
            println("title: "+it.title)
            println(audio.data == null)
            audio.data?.let {  call.respondBytes(audio.data!!, ContentType.Audio.Any)}
        }
    }

    get("/audio/list"){
        withContext(Dispatchers.IO){
            val audioController = AudioController().list()
            call.respondHtml {
                head {
                    title(audioController.size.toString())
                }
                body {
                    audioController.forEach {
                        ul {
                            li{
                                text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Route.deleteAll(){
    get("/audio/removeAll"){
        withContext(Dispatchers.IO){
            val audioController = AudioController()
            audioController.removeAll()
            call.respondRedirect("/audio/upload")
        }
    }
}