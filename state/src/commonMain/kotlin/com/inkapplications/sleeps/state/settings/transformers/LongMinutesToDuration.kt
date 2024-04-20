package com.inkapplications.sleeps.state.settings.transformers

import com.inkapplications.data.transformer.DataTransformer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Convert a Long value in minutes to a Duration value.
 */
internal object LongMinutesToDuration: DataTransformer<Long?, Duration?> {
    override fun transform(data: Long?): Duration? = data?.minutes
    override fun reverseTransform(data: Duration?): Long? = data?.inWholeMinutes
}
