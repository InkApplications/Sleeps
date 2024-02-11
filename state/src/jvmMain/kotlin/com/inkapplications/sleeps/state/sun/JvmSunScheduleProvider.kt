package com.inkapplications.sleeps.state.sun

import com.inkapplications.datetime.ZonedClock
import com.inkapplications.datetime.ZonedDate
import com.inkapplications.datetime.ZonedDateTime
import com.inkapplications.datetime.atZone
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.datetime.*
import java.util.Calendar
import java.util.TimeZone as JavaTimeZone

internal class JvmSunScheduleProvider(
    private val clock: ZonedClock,
) : SunScheduleProvider {
    private val calendar = Calendar.getInstance()

    override fun getNextSunriseForLocation(coordinates: GeoCoordinates): ZonedDateTime {
        val date = clock.zonedDateTime()
        val calculator = SunriseSunsetCalculator(
            Location(
                coordinates.latitude.asDecimal,
                coordinates.longitude.asDecimal,
            ),
            date.zone.id,
        )
        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.monthNumber - 1)
        calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
        calendar.timeZone = JavaTimeZone.getTimeZone(date.zone.id)

        val todaySunrise = calculator.getOfficialSunriseCalendarForDate(calendar)

        val sunrise = if (todaySunrise.timeInMillis < clock.now().toEpochMilliseconds()) {
            calendar.add(Calendar.DATE, 1)
            calculator.getOfficialSunriseCalendarForDate(calendar)
        } else todaySunrise

        return sunrise.timeInMillis
            .let { Instant.fromEpochMilliseconds(it) }
            .toLocalDateTime(date.zone)
            .atZone(date.zone)
    }
}
