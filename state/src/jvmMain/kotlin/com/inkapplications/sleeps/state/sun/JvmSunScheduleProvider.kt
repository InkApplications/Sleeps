package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedDate
import com.inkapplications.datetime.ZonedDateTime
import com.inkapplications.datetime.atZone
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.datetime.*
import java.util.Calendar
import java.util.TimeZone as JavaTimeZone

internal class JvmSunScheduleProvider : SunScheduleProvider {
    private val calendar = Calendar.getInstance()

    override fun getSunriseForLocation(coordinates: GeoCoordinates, date: ZonedDate): ZonedDateTime {
        val calculator = SunriseSunsetCalculator(
            Location(
                coordinates.latitude.asDecimal,
                coordinates.longitude.asDecimal,
            ),
            date.zone.id,
        )
        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.monthNumber)
        calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
        calendar.timeZone = JavaTimeZone.getTimeZone(date.zone.id)

        val sunrise = calculator.getOfficialSunriseCalendarForDate(calendar)

        return sunrise.timeInMillis
            .let { Instant.fromEpochMilliseconds(it) }
            .toLocalDateTime(date.zone)
            .atZone(date.zone)
    }
}
