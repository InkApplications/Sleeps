package com.inkapplications.sleeps.state.sun

import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SunStateProviderTest {
    @Test
    fun initial() = runTest {
        val provider = SunStateProvider(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getScheduleForLocation(coordinates: GeoCoordinates, date: LocalDate, timeZone: TimeZone): SunSchedule { TODO() }
            },
            clock = object: Clock {
                override fun now(): Instant = Instant.fromEpochMilliseconds(123)
            },
            timeZone = TimeZone.UTC,
            stateScope = backgroundScope,
            locationUpdates = flow { },
        )

        assertEquals(SunScheduleState.Initial, provider.sunState.value)
    }

    @Test
    fun nullLocation() = runTest {
        val provider = SunStateProvider(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getScheduleForLocation(coordinates: GeoCoordinates, date: LocalDate, timeZone: TimeZone): SunSchedule { TODO() }
            },
            clock = object: Clock {
                override fun now(): Instant = Instant.fromEpochMilliseconds(123)
            },
            timeZone = TimeZone.UTC,
            stateScope = backgroundScope,
            locationUpdates = flow { emit(null) },
        )

        val results = provider.sunState.take(2).toList()
        assertEquals(SunScheduleState.Initial, results[0])
        assertEquals(SunScheduleState.Unknown, results[1])
    }

    @Test
    fun knownLocation() = runTest {
        val provider = SunStateProvider(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getScheduleForLocation(coordinates: GeoCoordinates, date: LocalDate, timeZone: TimeZone): SunSchedule {
                    return SunSchedule(
                        sunrise = LocalTime(1, 2, 3),
                        sunset = LocalTime(4, 5, 6),
                    )
                }
            },
            clock = object: Clock {
                override fun now(): Instant = Instant.fromEpochMilliseconds(123)
            },
            timeZone = TimeZone.UTC,
            stateScope = backgroundScope,
            locationUpdates = flow { emit(GeoCoordinates(123.latitude, (456).longitude)) },
        )

        val results = provider.sunState.take(2).toList()
        assertEquals(SunScheduleState.Initial, results[0])
        val knownResult = results[1]
        assertTrue(knownResult is SunScheduleState.Known)
        assertEquals(LocalTime(1, 2, 3), knownResult.schedule.sunrise)
        assertEquals(LocalTime(4, 5, 6), knownResult.schedule.sunset)
    }
}
