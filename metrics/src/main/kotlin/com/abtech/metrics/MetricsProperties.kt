package com.abtech.metrics

import io.micrometer.core.instrument.Meter
import java.time.Duration

data class MetricsProperties(
    /**
     * Whether autoconfigured MeterRegistry implementations should be bound to the global
     * static registry on Metrics. For testing, set this to 'false' to maximize test
     * independence.
     */
    val isUseGlobalRegistry: Boolean = false,

    /**
     * Whether meter IDs starting with the specified name should be enabled. The longest
     * match wins, the key 'all' can also be used to configure all meters.
     */
    val enable: Map<String, Boolean> = LinkedHashMap(),

    /**
     * Common tags that are applied to every meter.
     */
    val tags: Map<String, String> = LinkedHashMap(),
    val distribution: Distribution = Distribution()

)

data class Distribution(
    /**
     * Whether meter IDs starting with the specified name should publish percentile
     * histograms. For monitoring systems that support aggregable percentile
     * calculation based on a histogram, this can be set to true. For other systems,
     * this has no effect. The longest match wins, the key 'all' can also be used to
     * configure all meters.
     */
    val percentilesHistogram: Map<String, Boolean> = LinkedHashMap(),

    /**
     * Specific computed non-aggregable percentiles to ship to the backend for meter
     * IDs starting-with the specified name. The longest match wins, the key 'all' can
     * also be used to configure all meters.
     */
    val percentiles: Map<String, List<Double>> = LinkedHashMap(),

    /**
     * Specific service-level objective boundaries for meter IDs starting with the
     * specified name. The longest match wins. Counters will be published for each
     * specified boundary. Values can be specified as a kotlin.time.Duration value
     * Duration formats
     * ns, nano, nanos, nanosecond, nanoseconds
     * us, micro, micros, microsecond, microseconds
     * ms, milli, millis, millisecond, milliseconds
     * s, second, seconds
     * m, minute, minutes
     * h, hour, hours
     * d, day, days
     */
    val slo: Map<String, List<MeterValue>> = LinkedHashMap(),

    /**
     * Minimum value that meter IDs starting with the specified name are expected to
     * observe. The longest match wins. Values can be specified as a double or as a
     * Duration value (for timer meters, defaulting to ms if no unit specified).
     */
    val minimumExpectedValue: Map<String, MeterValue> = LinkedHashMap(),

    /**
     * Maximum value that meter IDs starting with the specified name are expected to
     * observe. The longest match wins. Values can be specified as a double or as a
     * Duration value (for timer meters, defaulting to ms if no unit specified).
     */
    val maximumExpectedValue: Map<String, MeterValue> = LinkedHashMap(),

    /**
     * Maximum amount of time that samples for meter IDs starting with the specified
     * name are accumulated to decaying distribution statistics before they are reset
     * and rotated. The longest match wins, the key `all` can also be used to
     * configure all meters.
     */
    val expiry: Map<String, Duration> = LinkedHashMap(),

    /**
     * Number of histograms for meter IDs starting with the specified name to keep in
     * the ring buffer. The longest match wins, the key `all` can also be used to
     * configure all meters.
     */
    val bufferLength: Map<String, Int> = LinkedHashMap()
)

data class MeterValue(val value: Duration) {
    fun getValue(meterType: Meter.Type): Double? {
        return when (meterType) {
            Meter.Type.DISTRIBUTION_SUMMARY, Meter.Type.TIMER -> value.toNanos().toDouble()
            else -> null
        }
    }
}
