package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDateTime
import inkapplications.spondee.spatial.GeoCoordinates

interface SunScheduleProvider {
    fun getNextSunriseForLocation(
        coordinates: GeoCoordinates,
    ): ZonedDateTime
}
