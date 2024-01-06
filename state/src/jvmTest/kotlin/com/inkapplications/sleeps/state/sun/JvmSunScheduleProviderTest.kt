package com.inkapplications.sleeps.state.sun

import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmSunScheduleProviderTest {
    private val provider = JvmSunScheduleProvider()

    @Test
    fun sunriseCalculation() {
        val schedule = provider.getScheduleForLocation(
            coordinates = GeoCoordinates(40.730610.latitude, (-73.935242).longitude),
            date = LocalDate(2024, 1, 6),
            timeZone = TimeZone.of("America/New_York")
        )

        assertEquals(7, schedule.sunrise.hour)
        assertEquals(1, schedule.sunrise.minute)
        assertEquals(0, schedule.sunrise.second)
        assertEquals(17, schedule.sunset.hour)
        assertEquals(19, schedule.sunset.minute)
        assertEquals(0, schedule.sunset.second)
    }
}
