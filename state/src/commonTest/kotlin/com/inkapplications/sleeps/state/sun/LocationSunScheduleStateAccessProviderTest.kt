package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDate
import com.inkapplications.datetime.ZonedDateTime
import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.location.DummyLocationProvider
import com.inkapplications.sleeps.state.location.FakeLocationProvider
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class LocationSunScheduleStateAccessProviderTest {
    private val testDateTime = LocalTime(1, 2, 3).atDate(2004, 5, 6)
    private val testClock = object: Clock {
        override fun now(): Instant = testDateTime.atZone(TimeZone.UTC).minus(1.hours).instant
    }.atZone(TimeZone.UTC)

    @Test
    fun initial() = runTest {
        val provider = LocationSunState(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getSunriseForLocation(coordinates: GeoCoordinates, date: ZonedDate): ZonedDateTime { TODO() }
            },
            clock = testClock,
            stateScope = backgroundScope,
            locationProvider = DummyLocationProvider,
        )

        assertEquals(SunScheduleState.Initial, provider.sunState.value)
    }

    @Test
    fun nullLocation() = runTest {
        val provider = LocationSunState(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getSunriseForLocation(coordinates: GeoCoordinates, date: ZonedDate): ZonedDateTime {
                    return testDateTime.atZone(date.zone)
                }
            },
            clock = testClock,
            stateScope = backgroundScope,
            locationProvider = FakeLocationProvider(null),
        )

        val results = provider.sunState.take(2).toList()
        assertEquals(SunScheduleState.Initial, results[0])
        val unknownResult = results[1]
        assertTrue(unknownResult is SunScheduleState.Unknown)
        assertEquals(1, unknownResult.centralUsSunrise.hour)
        assertEquals(2, unknownResult.centralUsSunrise.minute)
        assertEquals(3, unknownResult.centralUsSunrise.second)
        assertEquals(2004, unknownResult.centralUsSunrise.year)
        assertEquals(5, unknownResult.centralUsSunrise.monthNumber)
        assertEquals(6, unknownResult.centralUsSunrise.dayOfMonth)
    }

    @Test
    fun knownLocation() = runTest {
        val provider = LocationSunState(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getSunriseForLocation(coordinates: GeoCoordinates, date: ZonedDate): ZonedDateTime {
                    return testDateTime.atZone(date.zone)
                }
            },
            clock = testClock,
            stateScope = backgroundScope,
            locationProvider = FakeLocationProvider(GeoCoordinates(123.latitude, (456).longitude)),
        )

        val results = provider.sunState.take(2).toList()
        assertEquals(SunScheduleState.Initial, results[0])
        val knownResult = results[1]
        assertTrue(knownResult is SunScheduleState.Known)
        assertEquals(1, knownResult.sunrise.hour)
        assertEquals(2, knownResult.sunrise.minute)
        assertEquals(3, knownResult.sunrise.second)
        assertEquals(2004, knownResult.sunrise.year)
        assertEquals(5, knownResult.sunrise.monthNumber)
        assertEquals(6, knownResult.sunrise.dayOfMonth)
        assertEquals(TimeZone.UTC, knownResult.sunrise.zone)
    }
}
