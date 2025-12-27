package com.evento.mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform