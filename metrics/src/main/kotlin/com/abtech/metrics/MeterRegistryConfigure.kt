package com.abtech.metrics

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resource
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.binder.MeterBinder
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.core.instrument.util.HierarchicalNameMapper
import io.micrometer.jmx.JmxConfig
import io.micrometer.jmx.JmxMeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry

fun meterRegistryResource(
    metrics: MetricsProperties,
    filters: List<MeterFilter> = emptyList(),
    binders: List<MeterBinder> = emptyList()
): Resource<Pair<MeterRegistry, PrometheusMeterRegistry>> =
    resource({
        val compositeMeterRegistry = CompositeMeterRegistry()
        val jmxMeterRegistry = JmxMeterRegistry(
            JmxConfig.DEFAULT,
            Clock.SYSTEM,
            HierarchicalNameMapper.DEFAULT
        )
        val simpleMeterRegistry = SimpleMeterRegistry()
        val prometheusMeterRegistry = PrometheusMeterRegistry(
            PrometheusConfig.DEFAULT,
            CollectorRegistry.defaultRegistry,
            Clock.SYSTEM
        )

        // Register meter registry
        compositeMeterRegistry.add(jmxMeterRegistry)
        compositeMeterRegistry.add(prometheusMeterRegistry)
        compositeMeterRegistry.add(simpleMeterRegistry)
        // Config
        val meterRegistryConfigure = MeterRegistryConfigure(
            filters = listOf(MetricsMeterFilter(metrics)) + filters,
            binders = binders,
            addToGlobalRegistry = false,
            hasCompositeMeterRegistry = true
        )
        configure(meterRegistryConfigure, compositeMeterRegistry)
        Pair(compositeMeterRegistry, prometheusMeterRegistry)
    }) { meterRegistryPair, _ ->
        meterRegistryPair.first.close()
    }

private fun configure(configure: MeterRegistryConfigure, meterRegistry: MeterRegistry) {
    // Customizers must be applied before binders, as they may add custom
    // tags or alter timer or summary configuration.
    addFilters(meterRegistry, configure.filters)
    addToGlobalRegistryIfNecessary(meterRegistry, configure.addToGlobalRegistry)
    if (isBindable(meterRegistry, configure.hasCompositeMeterRegistry)) {
        addBinders(meterRegistry, configure.binders)
    }
}

private fun addFilters(meterRegistry: MeterRegistry, filters: List<MeterFilter>) {
    filters.forEach { filter -> meterRegistry.config().meterFilter(filter) }
}

private fun isBindable(meterRegistry: MeterRegistry, hasCompositeMeterRegistry: Boolean): Boolean {
    return hasCompositeMeterRegistry || isCompositeMeterRegistry(meterRegistry)
}

private fun isCompositeMeterRegistry(meterRegistry: MeterRegistry): Boolean {
    return meterRegistry is CompositeMeterRegistry
}

private fun addToGlobalRegistryIfNecessary(meterRegistry: MeterRegistry, addToGlobalRegistry: Boolean) {
    if (addToGlobalRegistry && !isGlobalRegistry(meterRegistry)) {
        Metrics.addRegistry(meterRegistry)
    }
}

private fun isGlobalRegistry(meterRegistry: MeterRegistry): Boolean {
    return meterRegistry === Metrics.globalRegistry
}

private fun addBinders(meterRegistry: MeterRegistry, binders: List<MeterBinder>) {
    binders.forEach { binder -> binder.bindTo(meterRegistry) }
}

private data class MeterRegistryConfigure(
    val filters: List<MeterFilter>,
    val binders: List<MeterBinder>,
    val addToGlobalRegistry: Boolean,
    val hasCompositeMeterRegistry: Boolean
)
