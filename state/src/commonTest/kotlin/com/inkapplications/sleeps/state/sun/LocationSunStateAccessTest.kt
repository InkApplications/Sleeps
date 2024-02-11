package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDateTime
import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.location.DummyLocationAccess
import com.inkapplications.sleeps.state.location.FakeLocationAccess
import inkapplications.spondee.measure.metric.meters
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import regolith.sensors.location.LocationError
import regolith.sensors.location.LocationState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class LocationSunStateAccessTest {
    private val testDateTime = LocalTime(1, 2, 3).atDate(2004, 5, 6).atZone(TimeZone.UTC)

    @Test
    fun initial() = runTest {
        val provider = LocationSunState(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getNextSunriseForLocation(coordinates: GeoCoordinates): ZonedDateTime { TODO() }
            },
            stateScope = backgroundScope,
            locationAccess = DummyLocationAccess,
        )

        assertEquals(SunScheduleState.Initial, provider.sunState.value)
    }

    @Test
    fun nullLocation() = runTest {
        val provider = LocationSunState(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getNextSunriseForLocation(coordinates: GeoCoordinates): ZonedDateTime {
                    return testDateTime
                }
            },
            stateScope = backgroundScope,
            locationAccess = FakeLocationAccess(LocationState.Unknown(LocationError.Disabled)),
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

    @Test
    fun expiry() = runTest {
        val clock = object: Clock {
            override fun now(): Instant = testDateTime.minus(5.minutes).instant
        }.atZone(testDateTime.zone)
        val provider = LocationSunState(
            sunScheduleProvider = object: SunScheduleProvider {
                var requests = 0
                override fun getNextSunriseForLocation(coordinates: GeoCoordinates): ZonedDateTime {
                    return testDateTime.plus((requests++).days)
                }
            },
            stateScope = backgroundScope,
            locationAccess = FakeLocationAccess(LocationState.Unknown(LocationError.Disabled)),
        )

        val results = provider.sunState.take(2).toList()
        assertEquals(SunScheduleState.Initial, results[0])
        val unknownResult = results[1]
        assertTrue(unknownResult is SunScheduleState.Unknown)
        assertEquals(6, unknownResult.centralUsSunrise.dayOfMonth)

        advanceTimeBy(5.minutes)
        runCurrent()
        val newValue = provider.sunState.value
        assertTrue(newValue is SunScheduleState.Unknown)
        assertEquals(7, newValue.centralUsSunrise.dayOfMonth)
    }
}
