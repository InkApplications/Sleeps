package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.atZone
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
            date = LocalDate(2024, 1, 6).atZone(TimeZone.of("America/New_York")),
        )

        assertEquals(7, schedule.sunrise.hour)
        assertEquals(1, schedule.sunrise.minute)
        assertEquals(0, schedule.sunrise.second)
    }
}
