package com.inkapplications.sleeps.state.settings.validators

import com.inkapplications.data.validator.ValidationResult
import com.inkapplications.sleeps.state.settings.validators.PositiveDurationValidator.NegativeDurationError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class PositiveDurationValidatorTest {
    @Test
    fun passingValues() = runTest {
        assertEquals(ValidationResult.Success, PositiveDurationValidator.validate(123.minutes))
        assertEquals(ValidationResult.Success, PositiveDurationValidator.validate(0.minutes))
    }

    @Test
    fun negativeValue() = runTest {
        val result = PositiveDurationValidator.validate((-1).minutes)

        assertTrue(result is ValidationResult.Failed)
        assertEquals(NegativeDurationError, result.reasons.single())
    }
}
