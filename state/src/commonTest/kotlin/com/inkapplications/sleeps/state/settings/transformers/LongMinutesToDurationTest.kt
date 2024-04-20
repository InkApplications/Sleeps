package com.inkapplications.sleeps.state.settings.transformers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class LongMinutesToDurationTest {
    @Test
    fun validTransformations() {
        assertEquals(123L, LongMinutesToDuration.reverseTransform(123.minutes))
        assertEquals(0L, LongMinutesToDuration.reverseTransform(0.minutes))
        assertEquals(0L, LongMinutesToDuration.reverseTransform(0.5.minutes))
        assertEquals(123.minutes, LongMinutesToDuration.transform(123L))
        assertEquals(0.minutes, LongMinutesToDuration.transform(0L))
    }
}
