import java.io.File
import java.io.InputStream

fun File.copyInputStreamToFile(inputstream: InputStream){
    this.outputStream().use { fileOut ->
        inputstream.copyTo(fileOut)
    }
}

