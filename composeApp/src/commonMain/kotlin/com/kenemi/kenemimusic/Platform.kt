package com.kenemi.kenemimusic

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform