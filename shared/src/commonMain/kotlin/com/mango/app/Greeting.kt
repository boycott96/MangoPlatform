package com.mango.app

import com.mango.app.test.RocketLaunch
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import kotlin.random.Random


class Greeting {
    private val platform: Platform = getPlatform()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private fun daysUntilNewYear(): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val closestNewYear = LocalDate(today.year + 1, 1, 1)
        return today.daysUntil(closestNewYear)
    }

    private suspend fun loadData(): List<RocketLaunch> {
        return httpClient.get("https://api.spacexdata.com/v4/launches").body<List<RocketLaunch>>()
    }

    fun greet(callback: (List<String>) -> Unit) {
        val greetings = mutableListOf<String>()
        greetings.add(if (Random.nextBoolean()) "Hi!" else "Hello!")
        greetings.add("Guess what it is! > ${platform.name.reversed()}!")
        greetings.add("\nThere are only ${daysUntilNewYear()} days left until New Year! 🎆")

        CoroutineScope(Dispatchers.IO).launch {
            val rockets: List<RocketLaunch> = loadData()
            val lastSuccessLaunch = rockets.lastOrNull { it.launchSuccess == true }
            if (lastSuccessLaunch != null) {
                greetings.add("\nThe last successful launch was ${lastSuccessLaunch.launchDateUTC} 🚀")
            }
            withContext(Dispatchers.Main) {
                callback(greetings)
            }
        }
    }
}