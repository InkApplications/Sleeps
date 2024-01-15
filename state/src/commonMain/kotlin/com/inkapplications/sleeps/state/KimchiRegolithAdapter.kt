package com.inkapplications.sleeps.state

import kimchi.logger.KimchiLogger
import regolith.init.InitRunnerCallbacks
import regolith.init.InitTarget
import regolith.init.Initializer
import kotlin.reflect.KClass

/**
 * Adapt the Regolith callback interface to the Kimchi Logger.
 */
internal class KimchiRegolithAdapter(
    private val logger: KimchiLogger
): InitRunnerCallbacks {
    override fun onComplete() {
        logger.info("Initialization complete")
    }

    override fun onInitializerAwaitingTarget(initializer: Initializer, target: KClass<out InitTarget>) {
        logger.debug("[INIT_WAIT] ${initializer::class.simpleName} -> ${target.simpleName}")
    }

    override fun onInitializerComplete(initializer: Initializer) {
        logger.debug("[INIT_COMPLETE] ${initializer::class.simpleName}")
    }

    override fun onInitializerError(initializer: Initializer, error: Throwable) {
        logger.error("[INIT_ERROR] ${initializer::class.simpleName}", error)
    }

    override fun onTargetReached(target: InitTarget) {
        logger.debug("[TARGET] ${target::class.simpleName}")
    }
}
