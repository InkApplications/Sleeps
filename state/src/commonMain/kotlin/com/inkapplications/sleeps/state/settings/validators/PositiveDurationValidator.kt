package com.inkapplications.sleeps.state.settings.validators

import com.inkapplications.data.validator.DataValidator
import com.inkapplications.data.validator.ValidationError
import com.inkapplications.data.validator.ValidationResult
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Validates that a duration is a positive value.
 *
 * This is inclusive of zero values (will pass for zero).
 */
internal object PositiveDurationValidator: DataValidator<Duration> {
    override suspend fun validate(input: Duration): ValidationResult {
        return when {
            input < 0.seconds -> ValidationResult.Failed(NegativeDurationError)
            else -> ValidationResult.Success
        }
    }

    data object NegativeDurationError: ValidationError(
        message = "Duration cannot be negative",
    )
}
