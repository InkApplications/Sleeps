package com.inkapplications.sleeps.state.sun

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.datetime.*
import java.util.Calendar
import java.util.TimeZone as JavaTimeZone

internal class JvmSunScheduleProvider : SunScheduleProvider {
    private val calendar = Calendar.getInstance()
    override fun getScheduleForLocation(
        coordinates: GeoCoordinates,
        date: LocalDate,
        timeZone: TimeZone,
    ): SunSchedule {
        val calculator = SunriseSunsetCalculator(
            Location(
                coordinates.latitude.asDecimal,
                coordinates.longitude.asDecimal,
            ),
            timeZone.id,
        )
        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.monthNumber)
        calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
        calendar.timeZone = JavaTimeZone.getTimeZone(timeZone.toJavaZoneId())

        val sunrise = calculator.getOfficialSunriseCalendarForDate(calendar)
        val sunset = calculator.getOfficialSunsetCalendarForDate(calendar)

        return SunSchedule(
            sunrise = sunrise.toInstant().toKotlinInstant().toLocalDateTime(timeZone).time,
            sunset = sunset.toInstant().toKotlinInstant().toLocalDateTime(timeZone).time,
        )
    }
}
