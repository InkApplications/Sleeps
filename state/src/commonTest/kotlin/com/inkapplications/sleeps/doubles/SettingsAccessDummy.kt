package com.inkapplications.sleeps.doubles

import kotlinx.coroutines.flow.Flow
import regolith.data.settings.SettingsAccess
import regolith.data.settings.structure.PrimitiveSetting

object SettingsAccessDummy: SettingsAccess {
    override suspend fun <STORED> getSetting(setting: PrimitiveSetting<STORED>): STORED {
        TODO("Not implemented")
    }

    override fun <STORED> observeSetting(setting: PrimitiveSetting<STORED>): Flow<STORED> {
        TODO("Not implemented")
    }

    override suspend fun <STORED> writeSetting(setting: PrimitiveSetting<STORED>, value: STORED) {
        TODO("Not implemented")
    }
}
