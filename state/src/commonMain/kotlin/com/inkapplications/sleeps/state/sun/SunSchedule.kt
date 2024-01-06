package com.inkapplications.sleeps.state.sun

import kotlinx.datetime.LocalTime

data class SunSchedule(
    val sunrise: LocalTime,
    val sunset: LocalTime,
)
