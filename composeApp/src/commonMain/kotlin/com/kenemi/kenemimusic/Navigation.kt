package com.kenemi.kenemimusic

sealed class Screen {
    object PLAYER    : Screen()
    object SONGS     : Screen()
    object ARTISTS   : Screen()
    object ALBUMS    : Screen()
    object PLAYLISTS : Screen()
    object STATS     : Screen()
    object SETTINGS  : Screen()
    object FAVORITES : Screen()
    data class ARTIST_DETAIL(val artistName: String) : Screen()
    data class ALBUM_DETAIL(val albumId: Long) : Screen()
    data class PLAYLIST_DETAIL(val playlistId: Long) : Screen()
}

data class NavItem(val screen: Screen, val label: String, val icon: NavIcon)

enum class NavIcon {
    PLAYER, SONGS, ARTISTS, ALBUMS, PLAYLISTS, STATS, SETTINGS, FAVORITES
}

val navigationItems = listOf(
    NavItem(Screen.PLAYER,    "Lecteur",    NavIcon.PLAYER),
    NavItem(Screen.SONGS,     "Chansons",   NavIcon.SONGS),
    NavItem(Screen.ARTISTS,   "Artistes",   NavIcon.ARTISTS),
    NavItem(Screen.ALBUMS,    "Albums",     NavIcon.ALBUMS),
    NavItem(Screen.FAVORITES, "Favoris",    NavIcon.FAVORITES),
    NavItem(Screen.PLAYLISTS, "Playlists",  NavIcon.PLAYLISTS),
    NavItem(Screen.STATS,     "Stats",      NavIcon.STATS),
    NavItem(Screen.SETTINGS,  "Paramètres", NavIcon.SETTINGS),
)