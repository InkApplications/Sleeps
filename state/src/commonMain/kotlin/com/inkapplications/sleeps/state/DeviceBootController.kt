package com.inkapplications.sleeps.state

/**
 * Immediate, blocking actions to be performed when the device boots.
 *
 * This is different from app boot / initialization, as this is performed
 * only when the device itself is restarted, not the app.
 */
interface DeviceBootController {
    /**
     * Invoked when the device reboots.
     */
    fun onDeviceBoot()
}
