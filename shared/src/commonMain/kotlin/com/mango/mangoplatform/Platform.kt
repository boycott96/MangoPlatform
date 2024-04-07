package com.mango.mangoplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform