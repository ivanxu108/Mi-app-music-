package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.theme.MyApplicationTheme
import java.util.Calendar
import androidx.compose.foundation.Canvas
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.draw.blur

class MainActivity : ComponentActivity() {
    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    MusicApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun EqualizerIcon(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier.width(3.dp).height(12.dp).background(color, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(3.dp).height(16.dp).background(color, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(3.dp).height(8.dp).background(color, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(3.dp).height(14.dp).background(color, RoundedCornerShape(1.dp)))
    }
}

@Composable
fun MusicApp(
    viewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredSongs by viewModel.filteredSongs.collectAsStateWithLifecycle()
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isPreparing by viewModel.isPreparing.collectAsStateWithLifecycle()
    val elapsedTime by viewModel.elapsedTime.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val isShuffle by viewModel.isShuffle.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()

    var showRightPanel by remember { mutableStateOf(true) }
    var showMobilePlayerExpanded by remember { mutableStateOf(false) }

    val targetVibeColor = getVibeColorForSong(currentSong)
    val animatedVibeColor by animateColorAsState(
        targetValue = targetVibeColor,
        animationSpec = tween(durationMillis = 1000),
        label = "vibe_color_anim"
    )

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            animatedVibeColor.copy(alpha = 0.22f), // elegant subtle ambient glow
            Color(0xFF09090B).copy(alpha = 0.95f), // blend smoothly
            Color(0xFF09090B)                      // solid dark slate bottom
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        val isWideScreen = maxWidth >= 850.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Main workspace (Left Sidebar + Center Grid + Right Panel)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // 1. Sidebar Panel (only on wide screen)
                if (isWideScreen) {
                    SidebarLayout(
                        activeScreen = activeScreen,
                        onScreenSelect = { viewModel.setScreen(it) },
                        favoriteCount = songs.count { it.isFavorite },
                        onCreatePlaylist = { /* Mock action */ }
                    )
                }

                // 2. Central Main Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Header Search
                    MainHeader(
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        isWideScreen = isWideScreen,
                        showRightPanel = showRightPanel,
                        onToggleRightPanel = { showRightPanel = !showRightPanel }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Screen content switching
                    when (activeScreen) {
                        "Inicio" -> {
                            HomeScreen(
                                songs = filteredSongs,
                                currentSong = currentSong,
                                onPlaySong = { viewModel.playSong(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }
                        "Buscar" -> {
                            SearchScreen(
                                songs = filteredSongs,
                                onPlaySong = { viewModel.playSong(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }
                        "Biblioteca" -> {
                            LibraryScreen(
                                songs = songs,
                                currentSong = currentSong,
                                onPlaySong = { viewModel.playSong(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onAddLocalSong = { uri -> viewModel.addLocalSong(uri) }
                            )
                        }
                    }
                }

                // 3. Right Panel (Optional / Hideable, only on wide screen)
                if (isWideScreen && showRightPanel) {
                    RightSidebarLayout(
                        songs = songs,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        onSongSelect = { viewModel.playSong(it) },
                        onClose = { showRightPanel = false }
                    )
                }
            }

            // 4. Player Persistente (Bottom)
            if (currentSong != null) {
                if (isWideScreen) {
                    DesktopPlayer(
                        currentSong = currentSong!!,
                        isPlaying = isPlaying,
                        isPreparing = isPreparing,
                        elapsedTime = elapsedTime,
                        duration = duration,
                        volume = volume,
                        isShuffle = isShuffle,
                        repeatMode = repeatMode,
                        onPlayPauseToggle = { viewModel.togglePlayPause() },
                        onNext = { viewModel.playNext() },
                        onPrev = { viewModel.playPrevious() },
                        onShuffleToggle = { viewModel.toggleShuffle() },
                        onRepeatToggle = { viewModel.toggleRepeat() },
                        onSeek = { viewModel.seekTo(it) },
                        onVolumeChange = { viewModel.setVolume(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                } else {
                    // Compact mobile floating player with favorite capability
                    MobileMiniPlayer(
                        currentSong = currentSong!!,
                        isPlaying = isPlaying,
                        isPreparing = isPreparing,
                        elapsedTime = elapsedTime,
                        duration = duration,
                        onPlayPauseToggle = { viewModel.togglePlayPause() },
                        onNext = { viewModel.playNext() },
                        onToggleFavorite = { viewModel.toggleFavorite(currentSong!!) },
                        onClick = { showMobilePlayerExpanded = true }
                    )
                }
            }

            // 5. Mobile Bottom Navigation
            if (!isWideScreen) {
                // Border top separator to match border-t border-white/5
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.05f))
                )
                NavigationBar(
                    containerColor = Color(0xFF09090B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBarItem(
                        selected = activeScreen == "Inicio",
                        onClick = { viewModel.setScreen("Inicio") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF22C55E),
                            selectedTextColor = Color(0xFF22C55E),
                            unselectedIconColor = Color(0xFFA1A1AA),
                            unselectedTextColor = Color(0xFFA1A1AA),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = activeScreen == "Buscar",
                        onClick = { viewModel.setScreen("Buscar") },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        label = { Text("Buscar", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF22C55E),
                            selectedTextColor = Color(0xFF22C55E),
                            unselectedIconColor = Color(0xFFA1A1AA),
                            unselectedTextColor = Color(0xFFA1A1AA),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = activeScreen == "Biblioteca",
                        onClick = { viewModel.setScreen("Biblioteca") },
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Biblioteca") },
                        label = { Text("Biblioteca", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF22C55E),
                            selectedTextColor = Color(0xFF22C55E),
                            unselectedIconColor = Color(0xFFA1A1AA),
                            unselectedTextColor = Color(0xFFA1A1AA),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // Expanded player popup for Mobile
        AnimatedVisibility(
            visible = !isWideScreen && showMobilePlayerExpanded && currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            currentSong?.let { song ->
                MobileExpandedPlayer(
                    currentSong = song,
                    isPlaying = isPlaying,
                    isPreparing = isPreparing,
                    elapsedTime = elapsedTime,
                    duration = duration,
                    isShuffle = isShuffle,
                    repeatMode = repeatMode,
                    volume = volume,
                    onPlayPauseToggle = { viewModel.togglePlayPause() },
                    onNext = { viewModel.playNext() },
                    onPrev = { viewModel.playPrevious() },
                    onShuffleToggle = { viewModel.toggleShuffle() },
                    onRepeatToggle = { viewModel.toggleRepeat() },
                    onSeek = { viewModel.seekTo(it) },
                    onVolumeChange = { viewModel.setVolume(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(song) },
                    onDismiss = { showMobilePlayerExpanded = false }
                )
            }
        }

        // Toast-like Error handling Banner
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Snackbar(
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, contentDescription = "Error")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(errorMessage!!)
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. SIDEBAR NAVIGATION (Wide Screen)
// ==========================================
@Composable
fun SidebarLayout(
    activeScreen: String,
    onScreenSelect: (String) -> Unit,
    favoriteCount: Int,
    onCreatePlaylist: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(Color(0xFF09090B))
            .padding(20.dp)
    ) {
        // Logo with Equalizer icon prefix
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF22C55E)), // Tailwind green-500
                contentAlignment = Alignment.Center
            ) {
                EqualizerIcon(color = Color(0xFF09090B))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Pulse",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        // Navigation Group
        SidebarMenuItem(
            title = "Inicio",
            icon = Icons.Default.Home,
            selected = activeScreen == "Inicio",
            onClick = { onScreenSelect("Inicio") }
        )
        SidebarMenuItem(
            title = "Buscar",
            icon = Icons.Default.Search,
            selected = activeScreen == "Buscar",
            onClick = { onScreenSelect("Buscar") }
        )
        SidebarMenuItem(
            title = "Tu Biblioteca",
            icon = Icons.Default.LibraryMusic,
            selected = activeScreen == "Biblioteca",
            onClick = { onScreenSelect("Biblioteca") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Creator Section
        Text(
            text = "PLAYLISTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA1A1AA),
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        SidebarMenuItem(
            title = "Crear Playlist",
            icon = Icons.Default.PlaylistAdd,
            selected = false,
            onClick = onCreatePlaylist
        )

        SidebarMenuItem(
            title = "Tus Favoritas",
            icon = Icons.Default.Favorite,
            selected = activeScreen == "Biblioteca",
            badgeText = favoriteCount.toString(),
            onClick = { onScreenSelect("Biblioteca") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Premium Promo Glass-card inside sidebar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B).copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Consigue Premium",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF22C55E) // Vibrant Green
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Escucha sin anuncios y sin conexión de forma ilimitada.",
                    fontSize = 11.sp,
                    color = Color(0xFFA1A1AA),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun SidebarMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    val containerBg = if (selected) {
        Color(0xFF18181B).copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        Color(0xFF22C55E) // Tailwind green-500
    } else {
        Color(0xFFA1A1AA) // Tailwind zinc-400
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerBg)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) Color.White.copy(alpha = 0.05f) else Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("menu_item_${title.lowercase()}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF22C55E).copy(alpha = 0.15f),
                        CircleShape
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22C55E)
                )
            }
        }
    }
}
// ==========================================
// 2. MAIN HEADER (Search Bar + Action Row)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    isWideScreen: Boolean,
    showRightPanel: Boolean,
    onToggleRightPanel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Column: Navigation controls / Search
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (!isWideScreen) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF22C55E)), // Tailwind green-500
                    contentAlignment = Alignment.Center
                ) {
                    EqualizerIcon(color = Color(0xFF09090B))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Musicfy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar canciones, artistas...", fontSize = 13.sp, color = Color(0xFFA1A1AA)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar icon",
                        tint = Color(0xFFA1A1AA)
                    )
                },
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .testTag("search_bar_input"),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF22C55E),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.05f),
                    focusedContainerColor = Color(0xFF18181B).copy(alpha = 0.5f),
                    unfocusedContainerColor = Color(0xFF18181B).copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: Settings + Queue Toggles
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isWideScreen) {
                IconButton(
                    onClick = onToggleRightPanel,
                    modifier = Modifier.testTag("toggle_right_sidebar")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Cola de reproducción",
                        tint = if (showRightPanel) Color(0xFF22C55E) else Color(0xFFA1A1AA)
                    )
                }
            }

            IconButton(onClick = { /* Mock setting */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = Color(0xFFA1A1AA)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // User Profile Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF18181B))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), CircleShape)
                    .clickable { /* Profile Click */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// 3. HOME VIEW SCREEN
// ==========================================
@Composable
fun HomeScreen(
    songs: List<Song>,
    currentSong: Song?,
    onPlaySong: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item { WelcomeBanner() }

        // Recently Played list as a high-density 2-column grid
        item {
            val recentSongs = songs.filter { it.category == "Recientes" }
            if (recentSongs.isNotEmpty()) {
                Text(
                    text = "ESCUCHADO RECIENTEMENTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA1A1AA),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                val pairs = recentSongs.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pairs.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { song ->
                                RecentSongCard(
                                    song = song,
                                    isPlayingNow = song.id == currentSong?.id,
                                    onClick = { onPlaySong(song) },
                                    onToggleFavorite = { onToggleFavorite(song) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Recommended / Grid album covers
        item {
            Text(
                text = "RECOMENDADOS PARA TI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA1A1AA),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        items(songs.filter { it.category == "Recomendados" }.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { song ->
                    RecommendedSongRowItem(
                        song = song,
                        isPlayingNow = song.id == currentSong?.id,
                        onPlayClick = { onPlaySong(song) },
                        onFavClick = { onToggleFavorite(song) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Trends section
        item {
            Text(
                text = "EN TENDENCIA MUNDIAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA1A1AA),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }

        items(songs.filter { it.category == "Tendencias" }) { song ->
            SongListItem(
                song = song,
                isPlayingNow = song.id == currentSong?.id,
                onPlayClick = { onPlaySong(song) },
                onFavClick = { onToggleFavorite(song) }
            )
        }
    }
}

@Composable
fun WelcomeBanner() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour in 6..12 -> "¡Buenos días!"
        hour in 12..20 -> "¡Buenas tardes!"
        else -> "¡Buenas noches!"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF22C55E), // green-500
                        Color(0xFF064E3B)  // emerald-900
                    )
                )
            )
    ) {
        // Decorative radial blur shape in bottom-right
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                        center = Offset(700f, 300f),
                        radius = 200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "NUEVO LANZAMIENTO • EXCLUSIVO",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cyber Neon Nightmare",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 26.sp
                )
            }
            Text(
                text = "Streaming Now • $greeting",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RecentSongCard(
    song: Song,
    isPlayingNow: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("recent_song_${song.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B).copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (isPlayingNow) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        EqualizerIcon(color = Color(0xFF22C55E))
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isPlayingNow) Color(0xFF22C55E) else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    fontSize = 10.sp,
                    color = Color(0xFFA1A1AA),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (song.isFavorite) Color(0xFF22C55E) else Color(0xFFA1A1AA),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun RecommendedSongRowItem(
    song: Song,
    isPlayingNow: Boolean,
    onPlayClick: () -> Unit,
    onFavClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onPlayClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B).copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (isPlayingNow) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        EqualizerIcon(color = Color(0xFF22C55E), modifier = Modifier.size(24.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isPlayingNow) Color(0xFF22C55E) else Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        fontSize = 10.sp,
                        color = Color(0xFFA1A1AA),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onFavClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (song.isFavorite) Color(0xFF22C55E) else Color(0xFFA1A1AA),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. SEARCH SCREEN
// ==========================================
@Composable
fun SearchScreen(
    songs: List<Song>,
    onPlaySong: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit
) {
    val categories = listOf(
        Pair("Pop Latino", Color(0xFFE91E63)),
        Pair("Cyberpunk Beats", Color(0xFF9C27B0)),
        Pair("Chillhop Loft", Color(0xFF3F51B5)),
        Pair("Synthwave", Color(0xFF00BCD4)),
        Pair("Clásicos", Color(0xFF4CAF50)),
        Pair("Jazz & Lounge", Color(0xFFFF9800))
    )

    Column(modifier = Modifier.fillMaxSize()) {
        if (songs.isNotEmpty()) {
            Text(
                text = "Resultados de búsqueda",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(songs) { song ->
                    SongListItem(
                        song = song,
                        isPlayingNow = false,
                        onPlayClick = { onPlaySong(song) },
                        onFavClick = { onToggleFavorite(song) }
                    )
                }
            }
        } else {
            // General grid categories
            Text(
                text = "Explorar categorías",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { item ->
                    CategoryCard(title = item.first, color = item.second)
                }
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, color: Color) {
    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(14.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopStart)
        )
        // Simulated disc/decor
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .align(Alignment.BottomEnd)
        )
    }
}

// ==========================================
// 5. LIBRARY SCREEN
// ==========================================
@Composable
fun LibraryScreen(
    songs: List<Song>,
    currentSong: Song?,
    onPlaySong: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onAddLocalSong: (android.net.Uri) -> Unit
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            onAddLocalSong(uri)
        }
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Favoritas, 1: Archivos Locales

    val favorites = songs.filter { it.isFavorite }
    val localSongs = songs.filter { it.id.startsWith("local_") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Tu Biblioteca",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Import Button Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { filePickerLauncher.launch("audio/*") },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF22C55E).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir música",
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Añadir desde dispositivo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Importa canciones de tus archivos de audio",
                        fontSize = 11.sp,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }
        }

        // Tabs
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Favoritas", "Archivos Locales").forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF22C55E) else Color(0xFF18181B))
                        .border(
                            BorderStroke(
                                1.dp,
                                if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.05f)
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedTab = index }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF09090B) else Color.White
                    )
                }
            }
        }

        val displayedSongs = if (selectedTab == 0) favorites else localSongs

        if (displayedSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Default.FavoriteBorder else Icons.Default.QueueMusic,
                        contentDescription = "Vacio",
                        tint = Color(0xFFA1A1AA),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedTab == 0) "No tienes favoritas aún" else "No hay archivos locales",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedTab == 0) 
                            "Haz clic en el corazón de cualquier canción para guardarla." 
                            else "Importa tus propios MP3s y escúchalos sin límites.",
                        fontSize = 11.sp,
                        color = Color(0xFFA1A1AA),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayedSongs) { song ->
                    SongListItem(
                        song = song,
                        isPlayingNow = song.id == currentSong?.id,
                        onPlayClick = { onPlaySong(song) },
                        onFavClick = { onToggleFavorite(song) }
                    )
                }
            }
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    isPlayingNow: Boolean,
    onPlayClick: () -> Unit,
    onFavClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPlayingNow) Color(0xFF18181B) else Color.Transparent)
            .clickable(onClick = onPlayClick)
            .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPlayingNow) Color(0xFF22C55E) else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${song.artist} • ${song.album}",
                fontSize = 12.sp,
                color = Color(0xFFA1A1AA),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onFavClick) {
            Icon(
                imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorito",
                tint = if (song.isFavorite) Color(0xFF22C55E) else Color(0xFFA1A1AA),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = song.durationText,
            fontSize = 12.sp,
            color = Color(0xFFA1A1AA),
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}

// ==========================================
// 6. RIGHT SIDEBAR LAYOUT (Wide Screen)
// ==========================================
@Composable
fun RightSidebarLayout(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongSelect: (Song) -> Unit,
    onClose: () -> Unit
) {
    var rightTabActive by remember { mutableStateOf("Cola") } // "Cola", "Amigos"

    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Top Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(
                    text = "Cola",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rightTabActive == "Cola") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable { rightTabActive = "Cola" }
                        .padding(end = 16.dp)
                )

                Text(
                    text = "Actividad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rightTabActive == "Amigos") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { rightTabActive = "Amigos" }
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (rightTabActive) {
            "Cola" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(songs) { song ->
                        val isThisCurrent = song.id == currentSong?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isThisCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { onSongSelect(song) }
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = song.coverUrl,
                                contentDescription = song.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isThisCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = song.artist,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            "Amigos" -> {
                FriendActivityList()
            }
        }
    }
}

@Composable
fun FriendActivityList() {
    val friends = listOf(
        Triple("Carlos Mendoza", "Neon Waves", "Hace 2m"),
        Triple("María Gómez", "Midnight Drive", "EN DIRECTO 🟢"),
        Triple("Juan Pérez", "Vapor Horizon", "Hace 1h"),
        Triple("Ana Torres", "Deep Fusion", "Hace 3h")
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(friends) { friend ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glow avatar
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = "Friend",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.first,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Escuchando: ${friend.second}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = friend.third,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// 7. DESKTOP PERSISTENT BOTTOM PLAYER
// ==========================================
@Composable
fun DesktopPlayer(
    currentSong: Song,
    isPlaying: Boolean,
    isPreparing: Boolean,
    elapsedTime: Int,
    duration: Int,
    volume: Float,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    onPlayPauseToggle: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleFavorite: (Song) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF09090B))
    ) {
        // Top fine border-t border-white/5
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.05f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(89.dp)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Song Info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentSong.coverUrl,
                    contentDescription = currentSong.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.widthIn(max = 180.dp)) {
                    Text(
                        text = currentSong.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentSong.artist,
                        fontSize = 12.sp,
                        color = Color(0xFFA1A1AA),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { onToggleFavorite(currentSong) }) {
                    Icon(
                        imageVector = if (currentSong.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (currentSong.isFavorite) Color(0xFF22C55E) else Color(0xFFA1A1AA),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Center: Music Controls + Seekbar
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = onShuffleToggle) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Aleatorio",
                            tint = if (isShuffle) Color(0xFF22C55E) else Color(0xFFA1A1AA),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onPrev) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Anterior",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Main Play/Pause Action with Neon green circular style
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                            .clickable(enabled = !isPreparing, onClick = onPlayPauseToggle)
                            .testTag("desktop_play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPreparing) {
                            Text(
                                "...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Reproducir / Pausa",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    IconButton(onClick = onNext) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Siguiente",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = onRepeatToggle) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = "Repetir",
                            tint = if (repeatMode != RepeatMode.NONE) Color(0xFF22C55E) else Color(0xFFA1A1AA),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Seekbar
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(elapsedTime),
                        fontSize = 11.sp,
                        color = Color(0xFFA1A1AA)
                    )

                    Slider(
                        value = elapsedTime.toFloat(),
                        onValueChange = { onSeek(it.toInt()) },
                        valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF22C55E),
                            activeTrackColor = Color(0xFF22C55E),
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )

                    Text(
                        text = formatTime(duration),
                        fontSize = 11.sp,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }

            // Right: Volume controller
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onVolumeChange(if (volume > 0f) 0f else 0.5f) }) {
                    Icon(
                        imageVector = if (volume > 0f) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Volumen",
                        tint = Color(0xFFA1A1AA),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF22C55E),
                        activeTrackColor = Color(0xFF22C55E),
                        inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

// ==========================================
// 8. MOBILE MINI BOTTOM PLAYER (Compact Floating)
// ==========================================
@Composable
fun MobileMiniPlayer(
    currentSong: Song,
    isPlaying: Boolean,
    isPreparing: Boolean,
    elapsedTime: Int,
    duration: Int,
    onPlayPauseToggle: () -> Unit,
    onNext: () -> Unit,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("mobile_mini_player"),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Layer 1: Blurred album cover backdrop
            AsyncImage(
                model = currentSong.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(24.dp)
                    .background(Color(0xFF09090B).copy(alpha = 0.2f))
            )

            // Layer 2: Frost/acrylic dark translucent mask
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF18181B).copy(alpha = 0.75f),
                                Color(0xFF09090B).copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Layer 3: Elegant fluid wave animation flowing in the background
            FluidWaveAnimation(
                isPlaying = isPlaying,
                modifier = Modifier
                    .matchParentSize()
                    .align(Alignment.BottomCenter),
                color = Color(0xFF22C55E).copy(alpha = 0.12f)
            )

            // Layer 4: Content Layer (always sharp and legible)
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentSong.coverUrl,
                        contentDescription = currentSong.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentSong.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentSong.artist,
                            fontSize = 11.sp,
                            color = Color(0xFFA1A1AA),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Quick Fav button on Mobile Mini Player
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (currentSong.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (currentSong.isFavorite) Color(0xFF22C55E) else Color(0xFFA1A1AA),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onPlayPauseToggle,
                        enabled = !isPreparing,
                        modifier = Modifier.size(36.dp).testTag("mobile_mini_play_pause")
                    ) {
                        if (isPreparing) {
                            Text("...", color = Color(0xFF22C55E), fontSize = 12.sp)
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pausa",
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Siguiente",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Small timeline tracker bar at the bottom
                val progress = if (duration > 0) elapsedTime.toFloat() / duration else 0f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(Color(0xFF22C55E))
                    )
                }
            }
        }
    }
}

// ==========================================
// 9. MOBILE EXPANDED FULLSCREEN PLAYER
// ==========================================
@Composable
fun MobileExpandedPlayer(
    currentSong: Song,
    isPlaying: Boolean,
    isPreparing: Boolean,
    elapsedTime: Int,
    duration: Int,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    volume: Float,
    onPlayPauseToggle: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "REPRODUCIENDO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            IconButton(onClick = { /* Settings inside player */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Opciones",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Large Album Art Cover with Glassmorphism shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .shadow(12.dp)
        ) {
            AsyncImage(
                model = currentSong.coverUrl,
                contentDescription = currentSong.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Title and artist
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentSong.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = currentSong.artist,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (currentSong.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (currentSong.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Slider Timeline
        Slider(
            value = elapsedTime.toFloat(),
            onValueChange = { onSeek(it.toInt()) },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(elapsedTime),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTime(duration),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onShuffleToggle) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Aleatorio",
                    tint = if (isShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = onPrev) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Anterior",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Big circular center control
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(enabled = !isPreparing, onClick = onPlayPauseToggle),
                contentAlignment = Alignment.Center
            ) {
                if (isPreparing) {
                    Text("...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                } else {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pausa",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Siguiente",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = onRepeatToggle) {
                Icon(
                    imageVector = when (repeatMode) {
                        RepeatMode.ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    },
                    contentDescription = "Repetir",
                    tint = if (repeatMode != RepeatMode.NONE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Volumebar compact
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VolumeMute,
                contentDescription = "Mute",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Volumen max",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.5f))
    }
}

// Helpers
fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

fun getVibeColorForSong(song: Song?): Color {
    if (song == null) return Color(0xFF09090B) // Default black
    return when (song.id) {
        "1" -> Color(0xFF8B5CF6) // Retro Future / Purple
        "2" -> Color(0xFF3B82F6) // Tokyo Night / Blue
        "3" -> Color(0xFFEC4899) // Vector Grid / Pink/Fuchsia
        "4" -> Color(0xFF3F8CFF) // Nebula Stream / Indigo
        "5" -> Color(0xFFF97316) // Electro Core / Orange
        "6" -> Color(0xFF14B8A6) // Dreamwave / Teal
        "7" -> Color(0xFFD946EF) // Jazz Quartz / Magenta
        "8" -> Color(0xFF06B6D4) // Ocean Oasis / Cyan
        else -> {
            // Local custom song coloring based on deterministic title hash code
            val hashCode = song.title.hashCode()
            val hue = kotlin.math.abs(hashCode % 360).toFloat()
            Color.hsv(hue, 0.7f, 0.5f)
        }
    }
}

@Composable
fun FluidWaveAnimation(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF22C55E).copy(alpha = 0.2f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fluid_wave")
    val phaseShift by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * java.lang.Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Restart
            ),
            label = "phase"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = androidx.compose.ui.graphics.Path()
        
        path.moveTo(0f, height)
        for (x in 0..width.toInt() step 6) {
            val relativeX = x.toFloat() / width
            val sine = kotlin.math.sin(relativeX * 2.5 * java.lang.Math.PI + phaseShift).toFloat()
            // Dynamic subtle height wave
            val y = (sine * (height * 0.18f)) + (height * 0.65f)
            path.lineTo(x.toFloat(), y)
        }
        path.lineTo(width, height)
        path.close()
        
        drawPath(
            path = path,
            color = color
        )
    }
}
