data class User(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
)
data class UserPref(
    val userId: Int? = -1,
    val prefs: String? = null,
)

data class AudioFile(
    var id: Int = -1,
    var title: String = "",
    val type: AudioType = AudioType.SONG,
    val path: String = "",
    var data: ByteArray? = null,
)

data class SongArtist(
    val songId: Int = -1,
    val songArtist: Artist,
)

data class Playlist(
    val id: Int = -1,
    val title: String? = null,
    val releaseDate: String? = null,
    val ownerId: Int = -1,
)

data class Subscribes(
    val playlistId: Int = -1,
    val songId: Int = -1,
)

data class Artist(
    val artistId: Int = -1,
    val name: String? = null,
    //TODO
)

enum class AudioType {
    SONG,
    PPODCAST,
}
