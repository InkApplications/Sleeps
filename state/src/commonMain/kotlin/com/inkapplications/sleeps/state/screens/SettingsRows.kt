package com.inkapplications.sleeps.state.screens

import ink.ui.structures.elements.CheckBoxElement
import ink.ui.structures.elements.MenuRowElement
import ink.ui.structures.elements.SpinnerElement
import kotlin.time.Duration

/**
 * UI elements for individual rows on the settings screen.
 */
internal object SettingsRows {
    fun toggleRow(
        text: String,
        checked: Boolean,
        onClick: () -> Unit,
    ): MenuRowElement {
        return MenuRowElement(
            text = text,
            onClick = onClick,
            rightElement = CheckBoxElement(
                checked = checked,
                onClick = onClick,
            )
        )
    }

    fun durationRow(
        name: String,
        value: Duration,
        onIncrease: () -> Unit,
        onDecrease: () -> Unit,
    ): MenuRowElement {
        return MenuRowElement(
            text = name,
            rightElement = SpinnerElement(
                value = value.format(),
                hasPreviousValue = value.isPositive(),
                onNextValue = onIncrease,
                onPreviousValue = onDecrease,
            )
        )
    }

    private fun Duration.format() = (inWholeMinutes / 60f).toString()
}
