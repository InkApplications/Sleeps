package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDateTime
import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.location.FakeLocationAccess
import inkapplications.spondee.measure.metric.meters
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import regolith.sensors.location.LocationError
import regolith.sensors.location.LocationState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class LocationSunriseAccessTest {
    private val testDateTime = LocalTime(1, 2, 3).atDate(2004, 5, 6).atZone(TimeZone.UTC)

    @Test
    fun unknownLocation() = runTest {
        val provider = LocationSunriseAccess(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getNextSunriseForLocation(coordinates: GeoCoordinates): ZonedDateTime {
                    return testDateTime
                }
            },
            stateScope = backgroundScope,
            locationAccess = FakeLocationAccess(LocationState.Unknown(LocationError.Disabled)),
        )

        val results = async { provider.nextSunrise.first() }
        runCurrent()

        assertEquals(1, results.await().timestamp.hour)
        assertEquals(2, results.await().timestamp.minute)
        assertEquals(3, results.await().timestamp.second)
        assertEquals(2004, results.await().timestamp.year)
        assertEquals(5, results.await().timestamp.monthNumber)
        assertEquals(6, results.await().timestamp.dayOfMonth)
    }

    @Test
    fun knownLocation() = runTest {
        val provider = LocationSunriseAccess(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getNextSunriseForLocation(coordinates: GeoCoordinates): ZonedDateTime {
                    return testDateTime
                }
            },
            stateScope = backgroundScope,
            locationAccess = FakeLocationAccess(
                location = LocationState.Known(
                    coordinates = GeoCoordinates(123.latitude, (456).longitude),
                    accuracy = 0.meters,
                )
            ),
        )

        val results = async { provider.nextSunrise.first() }
        runCurrent()

        assertEquals(1, results.await().timestamp.hour)
        assertEquals(2, results.await().timestamp.minute)
        assertEquals(3, results.await().timestamp.second)
        assertEquals(2004, results.await().timestamp.year)
        assertEquals(5, results.await().timestamp.monthNumber)
        assertEquals(6, results.await().timestamp.dayOfMonth)
    }

    @Test
    fun expiry() = runTest {
        val clock = object: Clock {
            override fun now(): Instant = testDateTime.minus(5.minutes).instant
        }.atZone(testDateTime.zone)
        val provider = LocationSunriseAccess(
            sunScheduleProvider = object: SunScheduleProvider {
                var requests = 0
                override fun getNextSunriseForLocation(coordinates: GeoCoordinates): ZonedDateTime {
                    return testDateTime.plus((requests++).days)
                }
            },
            stateScope = backgroundScope,
            locationAccess = FakeLocationAccess(LocationState.Unknown(LocationError.Disabled)),
        )

        val firstResult = async { provider.nextSunrise.first() }
        runCurrent()
        assertEquals(6, firstResult.await().timestamp.dayOfMonth)

        advanceTimeBy(5.minutes)
        runCurrent()
        val newValue = async { provider.nextSunrise.first() }
        assertEquals(7, newValue.await().timestamp.dayOfMonth)
    }
}
