package com.kenemi.kenemimusic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

interface PlayerActions {
    fun play(song: Song)
    fun playAll(songs: List<Song>, startIndex: Int = 0)
    fun togglePlayPause()
    fun next()
    fun previous()
    fun seekTo(position: Float)
    fun toggleShuffle()
    fun toggleRepeat()
}

val LocalNavigate = staticCompositionLocalOf<(Screen) -> Unit> { {} }

val LocalPlayerActions = staticCompositionLocalOf<PlayerActions> {
    object : PlayerActions {
        override fun play(song: Song) {}
        override fun playAll(songs: List<Song>, startIndex: Int) {}
        override fun togglePlayPause() {}
        override fun next() {}
        override fun previous() {}
        override fun seekTo(position: Float) {}
        override fun toggleShuffle() {}
        override fun toggleRepeat() {}
    }
}

@Composable
fun App(
    isDesktop: Boolean = false,
    library: MusicLibrary = remember { MusicLibrary() },
    playerState: PlayerStateHolder = remember { PlayerStateHolder() },
    playerController: PlayerActions? = null,
    favoritesState: FavoritesState = remember { FavoritesState() },
    playlistsState: PlaylistsState = remember { PlaylistsState() },
    statsState: StatsState = remember { StatsState() },
    initialDarkTheme: Boolean = true
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.PLAYER) }
    var isDarkTheme by remember { mutableStateOf(initialDarkTheme) }
    val actions = playerController ?: LocalPlayerActions.current

    CompositionLocalProvider(
        LocalMusicLibrary provides library,
        LocalPlayerState provides playerState,
        LocalPlayerActions provides actions,
        LocalFavorites provides favoritesState,
        LocalPlaylists provides playlistsState,
        LocalStats provides statsState,
        LocalNavigate provides { currentScreen = it },
    ) {
        KenemiMusicTheme(darkTheme = isDarkTheme) {
            if (isDesktop) {
                DesktopLayout(currentScreen, { currentScreen = it }, isDarkTheme, { isDarkTheme = !isDarkTheme })
            } else {
                AndroidLayout(currentScreen, { currentScreen = it }, isDarkTheme, { isDarkTheme = !isDarkTheme })
            }
        }
    }
}

@Composable
fun ScreenContent(currentScreen: Screen, onScreenChange: (Screen) -> Unit,
                  isDarkTheme: Boolean, onThemeToggle: () -> Unit, isDesktop: Boolean) {
    when (val s = currentScreen) {
        is Screen.PLAYER    -> if (isDesktop) PlayerScreenDesktop() else PlayerScreenAndroid()
        is Screen.SONGS     -> SongsScreen()
        is Screen.ARTISTS   -> ArtistsScreen(onArtistClick = { onScreenChange(Screen.ARTIST_DETAIL(it)) })
        is Screen.ALBUMS    -> AlbumsScreen(onAlbumClick = { onScreenChange(Screen.ALBUM_DETAIL(it)) })
        is Screen.FAVORITES -> FavoritesScreen()
        is Screen.PLAYLISTS -> PlaylistsScreen(onPlaylistClick = { onScreenChange(Screen.PLAYLIST_DETAIL(it)) })
        is Screen.STATS     -> StatsScreen()
        is Screen.SETTINGS  -> SettingsScreen(isDarkTheme = isDarkTheme, onThemeToggle = onThemeToggle)
        is Screen.ARTIST_DETAIL   -> ArtistDetailScreen(artistName = s.artistName, onBack = { onScreenChange(Screen.ARTISTS) })
        is Screen.ALBUM_DETAIL    -> AlbumDetailScreen(albumId = s.albumId, onBack = { onScreenChange(Screen.ALBUMS) })
        is Screen.PLAYLIST_DETAIL -> PlaylistDetailScreen(playlistId = s.playlistId, onBack = { onScreenChange(Screen.PLAYLISTS) })
        is Screen.CURRENT_QUEUE   -> CurrentQueueScreen(onBack = { onScreenChange(Screen.PLAYER) })
    }
}

@Composable
fun DesktopLayout(currentScreen: Screen, onScreenChange: (Screen) -> Unit,
                  isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Sidebar(currentScreen = currentScreen, onScreenChange = onScreenChange)
        Box(modifier = Modifier.fillMaxHeight().width(0.5.dp).background(MaterialTheme.colorScheme.outline))
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenContent(currentScreen, onScreenChange, isDarkTheme, onThemeToggle, isDesktop = true)
        }
    }
}

@Composable
fun AndroidLayout(currentScreen: Screen, onScreenChange: (Screen) -> Unit,
                  isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.weight(1f)) {
            ScreenContent(currentScreen, onScreenChange, isDarkTheme, onThemeToggle, isDesktop = false)
        }
        BottomNavBar(currentScreen = currentScreen, onScreenChange = onScreenChange)
    }
}

@Composable
fun Sidebar(currentScreen: Screen, onScreenChange: (Screen) -> Unit) {
    val activeScreen = when (currentScreen) {
        is Screen.ARTIST_DETAIL   -> Screen.ARTISTS
        is Screen.ALBUM_DETAIL    -> Screen.ALBUMS
        is Screen.PLAYLIST_DETAIL -> Screen.PLAYLISTS
        else -> currentScreen
    }
    Column(modifier = Modifier.width(200.dp).fillMaxHeight()
        .background(MaterialTheme.colorScheme.surface).padding(vertical = 16.dp)) {
        Text("KENEMI MUSIC", fontSize = 12.sp, fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.08.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        navigationItems.filter { it.screen !is Screen.SETTINGS }.forEach { item ->
            SidebarItem(item = item, isSelected = activeScreen == item.screen,
                onClick = { onScreenChange(item.screen) })
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .height(0.5.dp).background(MaterialTheme.colorScheme.outline))
        Spacer(modifier = Modifier.height(8.dp))
        SidebarItem(item = NavItem(Screen.SETTINGS, "Paramètres", NavIcon.SETTINGS),
            isSelected = currentScreen is Screen.SETTINGS,
            onClick = { onScreenChange(Screen.SETTINGS) })
    }
}

@Composable
fun SidebarItem(item: NavItem, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .background(bgColor).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.width(2.dp).height(20.dp).background(
            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent))
        NavIconComposable(icon = item.icon, tint = textColor)
        Text(text = item.label, fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.W500 else FontWeight.Normal, color = textColor)
    }
}

@Composable
fun BottomNavBar(currentScreen: Screen, onScreenChange: (Screen) -> Unit) {
    val activeScreen = when (currentScreen) {
        is Screen.ARTIST_DETAIL   -> Screen.ARTISTS
        is Screen.ALBUM_DETAIL    -> Screen.ALBUMS
        is Screen.PLAYLIST_DETAIL -> Screen.PLAYLISTS
        else -> currentScreen
    }
    val bottomItems = navigationItems.filter {
        it.screen in listOf(Screen.PLAYER, Screen.ARTISTS, Screen.ALBUMS, Screen.PLAYLISTS, Screen.SETTINGS)
    }
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
        bottomItems.forEach { item ->
            NavigationBarItem(selected = activeScreen == item.screen,
                onClick = { onScreenChange(item.screen) },
                icon = { NavIconComposable(icon = item.icon,
                    tint = if (activeScreen == item.screen) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant) },
                label = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ))
        }
    }
}

@Composable
fun NavIconComposable(icon: NavIcon, tint: Color) {
    val imageVector = when (icon) {
        NavIcon.PLAYER    -> Icons.Player
        NavIcon.SONGS     -> Icons.Songs
        NavIcon.ARTISTS   -> Icons.Artists
        NavIcon.ALBUMS    -> Icons.Albums
        NavIcon.PLAYLISTS -> Icons.Playlists
        NavIcon.STATS     -> Icons.Stats
        NavIcon.SETTINGS  -> Icons.Settings
        NavIcon.FAVORITES -> Icons.Heart
    }
    Icon(imageVector = imageVector, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}