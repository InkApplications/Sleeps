package com.inkapplications.sleeps.state.sun

import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

interface SunScheduleProvider {
    fun getScheduleForLocation(
        coordinates: GeoCoordinates,
        date: LocalDate,
        timeZone: TimeZone,
    ): SunSchedule
}
