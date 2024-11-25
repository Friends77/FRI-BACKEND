package com.friends

import io.kotest.core.config.AbstractProjectConfig

class KotestProjectConfig : AbstractProjectConfig() {
    override val parallelism = (Runtime.getRuntime().availableProcessors() * 1.5).toInt()
}
