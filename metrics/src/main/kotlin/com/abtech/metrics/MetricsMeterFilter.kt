package com.abtech.metrics

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.config.MeterFilterReply
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import java.util.function.Supplier

class MetricsMeterFilter(private val properties: MetricsProperties) : MeterFilter {
    private val mapFilter: MeterFilter = createMapFilter(properties.tags)

    override fun accept(id: Meter.Id): MeterFilterReply {
        val enabled = lookupWithFallbackToAll(properties.enable, id, true)!!
        return if (enabled) MeterFilterReply.NEUTRAL else MeterFilterReply.DENY
    }

    override fun map(id: Meter.Id): Meter.Id {
        return mapFilter.map(id)
    }

    override fun configure(id: Meter.Id, config: DistributionStatisticConfig): DistributionStatisticConfig {
        val distribution: Distribution = properties.distribution
        return DistributionStatisticConfig.builder()
            .apply {
                lookupWithFallbackToAll(
                    distribution.percentilesHistogram,
                    id,
                    null
                )?.let(this::percentilesHistogram)
                lookupWithFallbackToAll(
                    distribution.percentiles,
                    id,
                    null
                )?.let { this.percentiles(*it.toDoubleArray()) }

                convertServiceLevelObjectives(
                    id.type,
                    lookup(distribution.slo, id, null)
                )?.let { this.serviceLevelObjectives(*it.toDoubleArray()) }
                lookupWithFallbackToAll(distribution.expiry, id, null)?.let(this::expiry)
                convertMeterValue(
                    id.type,
                    lookup(distribution.minimumExpectedValue, id, null)
                )?.let(this::minimumExpectedValue)
                convertMeterValue(
                    id.type,
                    lookup(distribution.maximumExpectedValue, id, null)
                )?.let(this::maximumExpectedValue)
                lookupWithFallbackToAll(distribution.bufferLength, id, null)?.let(this::bufferLength)
            }
            .build().merge(config)
    }

    private fun convertServiceLevelObjectives(
        meterType: Meter.Type,
        slo: List<MeterValue>?
    ): List<Double>? {
        return slo?.let {
            it.mapNotNull { candidate -> candidate.getValue(meterType) }
        }
    }

    private fun convertMeterValue(meterType: Meter.Type, value: MeterValue?): Double? {
        return value?.getValue(meterType)
    }

    private fun <T> lookup(values: Map<String, T>, id: Meter.Id, defaultValue: T?): T? {
        return if (values.isEmpty()) {
            defaultValue
        } else doLookup(values, id) { defaultValue }
    }

    private fun <T> lookupWithFallbackToAll(values: Map<String, T>, id: Meter.Id, defaultValue: T?): T? {
        return if (values.isEmpty()) {
            defaultValue
        } else doLookup(values, id) { values.getOrDefault("all", defaultValue) }
    }

    private fun <T> doLookup(values: Map<String, T>, id: Meter.Id, defaultValue: Supplier<T>): T {
        var name = id.name
        while (!name.isNullOrEmpty()) {
            val result = values[name]
            if (result != null) {
                return result
            }
            val lastDot = name.lastIndexOf('.')
            name = if (lastDot != -1) name.substring(0, lastDot) else ""
        }
        return defaultValue.get()
    }

    private fun createMapFilter(tags: Map<String, String>): MeterFilter {
        return if (tags.isEmpty()) object : MeterFilter {}
        else MeterFilter.commonTags(Tags.of(tags.entries.stream().map { Tag.of(it.key, it.value) }.toList()))
    }
}
