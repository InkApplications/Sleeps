package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.atZone
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmSunScheduleProviderTest {
    val now = LocalDate(2024, 1, 6).atTime(LocalTime(1, 2, 3)).atZone(TimeZone.of("America/New_York"))
    private val provider = JvmSunScheduleProvider(object: Clock {
        override fun now(): Instant = now.instant
    }.atZone(now.zone))

    @Test
    fun sunriseCalculation() {
        val schedule = provider.getNextSunriseForLocation(
            coordinates = GeoCoordinates(40.730610.latitude, (-73.935242).longitude),
        )

        assertEquals(7, schedule.hour)
        assertEquals(20, schedule.minute)
        assertEquals(0, schedule.second)
        assertEquals(1, schedule.monthNumber)
        assertEquals(7, schedule.dayOfMonth)
        assertEquals(2024, schedule.year)
    }
}
