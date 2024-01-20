package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDate
import com.inkapplications.datetime.ZonedDateTime
import inkapplications.spondee.spatial.GeoCoordinates

interface SunScheduleProvider {
    fun getSunriseForLocation(
        coordinates: GeoCoordinates,
        date: ZonedDate,
    ): ZonedDateTime
}
