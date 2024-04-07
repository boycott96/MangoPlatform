package com.mango.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform