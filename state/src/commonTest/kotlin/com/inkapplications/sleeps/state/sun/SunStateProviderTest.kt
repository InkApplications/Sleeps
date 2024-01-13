package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDate
import com.inkapplications.datetime.atZone
import com.inkapplications.sleeps.state.location.DummyLocationProvider
import com.inkapplications.sleeps.state.location.FakeLocationProvider
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
                override fun getScheduleForLocation(coordinates: GeoCoordinates, date: ZonedDate): SunSchedule { TODO() }
            },
            clock = object: Clock {
                override fun now(): Instant = Instant.fromEpochMilliseconds(123)
            }.atZone(TimeZone.UTC),
            stateScope = backgroundScope,
            locationProvider = DummyLocationProvider,
        )

        assertEquals(SunScheduleState.Initial, provider.sunState.value)
    }

    @Test
    fun nullLocation() = runTest {
        val provider = SunStateProvider(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getScheduleForLocation(coordinates: GeoCoordinates, date: ZonedDate): SunSchedule { TODO() }
            },
            clock = object: Clock {
                override fun now(): Instant = Instant.fromEpochMilliseconds(123)
            }.atZone(TimeZone.UTC),
            stateScope = backgroundScope,
            locationProvider = FakeLocationProvider(null),
        )

        val results = provider.sunState.take(2).toList()
        assertEquals(SunScheduleState.Initial, results[0])
        assertEquals(SunScheduleState.Unknown, results[1])
    }

    @Test
    fun knownLocation() = runTest {
        val provider = SunStateProvider(
            sunScheduleProvider = object: SunScheduleProvider {
                override fun getScheduleForLocation(coordinates: GeoCoordinates, date: ZonedDate): SunSchedule {
                    return SunSchedule(
                        sunrise = LocalTime(1, 2, 3).atZone(date.zone),
                    )
                }
            },
            clock = object: Clock {
                override fun now(): Instant = Instant.fromEpochMilliseconds(123)
            }.atZone(TimeZone.UTC),
            stateScope = backgroundScope,
            locationProvider = FakeLocationProvider(GeoCoordinates(123.latitude, (456).longitude)),
        )

        val results = provider.sunState.take(2).toList()
        assertEquals(SunScheduleState.Initial, results[0])
        val knownResult = results[1]
        assertTrue(knownResult is SunScheduleState.Known)
        assertEquals(1, knownResult.schedule.sunrise.hour)
        assertEquals(2, knownResult.schedule.sunrise.minute)
        assertEquals(3, knownResult.schedule.sunrise.second)
        assertEquals(TimeZone.UTC, knownResult.schedule.sunrise.zone)
    }
}
