package com.inkapplications.sleeps.state.settings

import com.inkapplications.data.transformer.DefaultingTransformer
import com.inkapplications.data.transformer.then
import com.inkapplications.sleeps.state.settings.transformers.LongMinutesToDuration
import com.inkapplications.sleeps.state.settings.validators.PositiveDurationValidator
import regolith.data.settings.structure.LongData
import kotlin.time.Duration

/**
 * Creates a setting for a Duration object that is stored as minutes.
 */
internal fun MinutesDurationSetting(
    key: String,
    name: String,
    defaultValue: Duration,
): LongData<Duration> = LongData(
    key = key,
    name = name,
    defaultValue = defaultValue,
    dataTransformer = LongMinutesToDuration.then(DefaultingTransformer(defaultValue)),
    inputValidator = PositiveDurationValidator,
)
