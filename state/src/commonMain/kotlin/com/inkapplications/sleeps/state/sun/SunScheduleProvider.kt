package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDate
import inkapplications.spondee.spatial.GeoCoordinates

interface SunScheduleProvider {
    fun getScheduleForLocation(
        coordinates: GeoCoordinates,
        date: ZonedDate,
    ): SunSchedule
}
