package com.example

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val coverUrl: String,
    val audioUrl: String,
    val isFavorite: Boolean = false,
    val durationText: String = "3:30",
    val category: String = "Recomendados"
)

enum class RepeatMode {
    NONE, ONE, ALL
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val songDao = db.songDao()

    private val originalSongs = listOf(
        Song(
            id = "1",
            title = "Neon Waves",
            artist = "Retro Future",
            album = "Laser Horizon",
            coverUrl = "https://images.unsplash.com/photo-1614149162883-504ce4d13909?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            durationText = "6:12",
            category = "Recientes"
        ),
        Song(
            id = "2",
            title = "Midnight Drive",
            artist = "Tokyo Night",
            album = "City Pop Beats",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            durationText = "7:05",
            category = "Recientes"
        ),
        Song(
            id = "3",
            title = "Cyberpunk Lounge",
            artist = "Vector Grid",
            album = "Digital Mirage",
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            durationText = "5:44",
            category = "Recomendados"
        ),
        Song(
            id = "4",
            title = "Starlight Echo",
            artist = "Nebula Stream",
            album = "Deep Cosmic",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            durationText = "5:02",
            category = "Recomendados"
        ),
        Song(
            id = "5",
            title = "Solar Flare",
            artist = "Electro Core",
            album = "Atomic Pulse",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            durationText = "5:53",
            category = "Recomendados"
        ),
        Song(
            id = "6",
            title = "Vapor Horizon",
            artist = "Dreamwave",
            album = "Retro Nostalgia",
            coverUrl = "https://images.unsplash.com/photo-1511192336575-5a79af67a629?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            durationText = "5:44",
            category = "Recomendados"
        ),
        Song(
            id = "7",
            title = "Deep Fusion",
            artist = "Jazz Quartz",
            album = "Smooth Grooves",
            coverUrl = "https://images.unsplash.com/photo-1511192336575-5a79af67a629?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            durationText = "5:02",
            category = "Tendencias"
        ),
        Song(
            id = "8",
            title = "Crystal Rain",
            artist = "Ocean Oasis",
            album = "Nature Relax",
            coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?q=80&w=300&auto=format&fit=crop",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            durationText = "6:18",
            category = "Tendencias"
        )
    )

    private val mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener { mp ->
            _isPlaying.value = true
            _isPreparing.value = false
            _duration.value = mp.duration
            mp.start()
        }
        setOnCompletionListener {
            handleSongCompletion()
        }
        setOnErrorListener { _, what, extra ->
            _isPreparing.value = false
            _isPlaying.value = false
            _errorMessage.value = "Error al reproducir audio (código: $what)"
            true
        }
    }

    // State Flows
    val songs: StateFlow<List<Song>> = songDao.getAllSongsFlow()
        .map { entities ->
            entities.map {
                Song(
                    id = it.id,
                    title = it.title,
                    artist = it.artist,
                    album = it.album,
                    coverUrl = it.coverUrl,
                    audioUrl = it.audioUrl,
                    isFavorite = it.isFavorite,
                    durationText = it.durationText,
                    category = it.category
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Filtered songs
    val filteredSongs: StateFlow<List<Song>> = combine(songs, _searchQuery) { songList, query ->
        if (query.isBlank()) {
            songList
        } else {
            songList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentSongIndex = MutableStateFlow(-1)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex

    val currentSong: StateFlow<Song?> = combine(songs, _currentSongIndex) { songList, index ->
        if (index in songList.indices) songList[index] else null
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isPreparing = MutableStateFlow(false)
    val isPreparing: StateFlow<Boolean> = _isPreparing

    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration

    private val _volume = MutableStateFlow(1.0f)
    val volume: StateFlow<Float> = _volume

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _activeScreen = MutableStateFlow("Inicio") // "Inicio", "Buscar", "Biblioteca"
    val activeScreen: StateFlow<String> = _activeScreen

    private var progressJob: Job? = null

    init {
        viewModelScope.launch {
            val existing = songDao.getAllSongs()
            if (existing.isEmpty()) {
                val entities = originalSongs.map {
                    SongEntity(
                        id = it.id,
                        title = it.title,
                        artist = it.artist,
                        album = it.album,
                        coverUrl = it.coverUrl,
                        audioUrl = it.audioUrl,
                        isFavorite = it.isFavorite,
                        durationText = it.durationText,
                        category = it.category,
                        isLocal = false
                    )
                }
                songDao.insertSongs(entities)
            }
        }
        startProgressTracker()
    }

    fun setScreen(screenName: String) {
        _activeScreen.value = screenName
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playSong(song: Song) {
        val songList = songs.value
        val index = songList.indexOfFirst { it.id == song.id }
        if (index != -1) {
            playSongAtIndex(index)
        }
    }

    fun togglePlayPause() {
        if (_currentSongIndex.value == -1 && songs.value.isNotEmpty()) {
            // No song loaded, load first song
            playSongAtIndex(0)
            return
        }

        if (_isPreparing.value) return

        if (_isPlaying.value) {
            mediaPlayer.pause()
            _isPlaying.value = false
        } else {
            mediaPlayer.start()
            _isPlaying.value = true
        }
    }

    fun playNext() {
        val songList = songs.value
        if (songList.isEmpty()) return

        if (_isShuffle.value) {
            val randomIndex = songList.indices.random()
            playSongAtIndex(randomIndex)
        } else {
            val nextIndex = if (_currentSongIndex.value + 1 in songList.indices) {
                _currentSongIndex.value + 1
            } else {
                0 // Loop back to the first song
            }
            playSongAtIndex(nextIndex)
        }
    }

    fun playPrevious() {
        val songList = songs.value
        if (songList.isEmpty()) return

        // If the song is already running for more than 3 seconds, reset the current song
        if (_elapsedTime.value > 3000) {
            seekTo(0)
            return
        }

        if (_isShuffle.value) {
            val randomIndex = songList.indices.random()
            playSongAtIndex(randomIndex)
        } else {
            val prevIndex = if (_currentSongIndex.value - 1 in songList.indices) {
                _currentSongIndex.value - 1
            } else {
                songList.size - 1 // Go to last song
            }
            playSongAtIndex(prevIndex)
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.NONE -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.NONE
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            songDao.updateFavorite(song.id, !song.isFavorite)
        }
    }

    fun seekTo(positionMs: Int) {
        if (!_isPreparing.value && _currentSongIndex.value != -1) {
            mediaPlayer.seekTo(positionMs)
            _elapsedTime.value = positionMs
        }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        mediaPlayer.setVolume(clamped, clamped)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun playSongAtIndex(index: Int) {
        val songList = songs.value
        if (index !in songList.indices) return

        _currentSongIndex.value = index
        val song = songList[index]

        viewModelScope.launch {
            try {
                _errorMessage.value = null
                _isPreparing.value = true
                _isPlaying.value = false
                _elapsedTime.value = 0
                _duration.value = 0

                mediaPlayer.reset()
                mediaPlayer.setDataSource(song.audioUrl)
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                _isPreparing.value = false
                _errorMessage.value = "Error al inicializar la canción: ${e.localizedMessage}"
            }
        }
    }

    private fun handleSongCompletion() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                // Seek to start and play again
                seekTo(0)
                mediaPlayer.start()
                _isPlaying.value = true
            }
            RepeatMode.ALL -> {
                playNext()
            }
            RepeatMode.NONE -> {
                // If last song, stop. Otherwise, go to next.
                val songList = songs.value
                if (_currentSongIndex.value == songList.size - 1) {
                    _isPlaying.value = false
                    seekTo(0)
                } else {
                    playNext()
                }
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                if (_isPlaying.value && !_isPreparing.value) {
                    try {
                        _elapsedTime.value = mediaPlayer.currentPosition
                    } catch (e: Exception) {
                        // Ignore any media player state exception in tracker
                    }
                }
                delay(500)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        try {
            mediaPlayer.release()
        } catch (e: Exception) {
            // Ignore release exceptions
        }
    }

    // Local song import functions
    fun addLocalSong(uri: android.net.Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val resolver = context.contentResolver
            
            // Extract metadata
            var title = "Canción Local"
            var artist = "Artista Desconocido"
            var album = "Álbum Desconocido"
            var durationMs = 0L
            var coverPath = ""
            
            val retriever = android.media.MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                title = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE) 
                    ?: getFileName(context, uri) 
                    ?: "Canción Local"
                artist = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Artista Desconocido"
                album = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Álbum Local"
                val durStr = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                if (durStr != null) {
                    durationMs = durStr.toLongOrNull() ?: 0L
                }
                
                // Extract embedded picture
                val picBytes = retriever.embeddedPicture
                if (picBytes != null) {
                    val coverFile = java.io.File(context.cacheDir, "cover_${System.currentTimeMillis()}.jpg")
                    coverFile.outputStream().use { it.write(picBytes) }
                    coverPath = "file://${coverFile.absolutePath}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    retriever.release()
                } catch (e: Exception) {}
            }
            
            // If no cover extracted, we can use a nice default local or random online cover
            if (coverPath.isEmpty()) {
                coverPath = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=300&auto=format&fit=crop"
            }
            
            // Copy audio file to local files directory for persistence
            val localAudioFile = java.io.File(context.filesDir, "audio_${System.currentTimeMillis()}.mp3")
            try {
                resolver.openInputStream(uri)?.use { input ->
                    localAudioFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al copiar archivo: ${e.localizedMessage}"
                return@launch
            }
            
            val durationText = formatDuration(durationMs)
            val newId = "local_${System.currentTimeMillis()}"
            
            val songEntity = SongEntity(
                id = newId,
                title = title,
                artist = artist,
                album = album,
                coverUrl = coverPath,
                audioUrl = localAudioFile.absolutePath,
                isFavorite = false,
                durationText = durationText,
                category = "Biblioteca",
                isLocal = true
            )
            
            songDao.insertSong(songEntity)
        }
    }

    private fun getFileName(context: android.content.Context, uri: android.net.Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        name = it.getString(index)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                name = name?.substring(cut + 1)
            }
        }
        if (name != null && name!!.contains(".")) {
            name = name!!.substringBeforeLast(".")
        }
        return name
    }

    private fun formatDuration(ms: Long): String {
        if (ms <= 0) return "3:00"
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
