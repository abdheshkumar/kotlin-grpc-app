package com.abtech.metrics

import com.sksamuel.hoplite.*
import com.sksamuel.hoplite.decoder.NonNullableLeafDecoder
import com.sksamuel.hoplite.decoder.toValidated
import com.sksamuel.hoplite.fp.Validated
import com.sksamuel.hoplite.fp.invalid
import com.sksamuel.hoplite.fp.valid
import com.sksamuel.hoplite.time.parseDuration
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.reflect.KType

class MeterValueDecoder : NonNullableLeafDecoder<MeterValue> {

    override fun supports(type: KType): Boolean = type.classifier == MeterValue::class
    override fun safeLeafDecode(
        node: Node,
        type: KType,
        context: DecoderContext
    ): ConfigResult<MeterValue> = when (node) {
        is StringNode -> {
            when (val v = parseDuration(node.value)) {
                is Validated.Invalid ->
                    kotlin.runCatching { node.value.toDouble() }
                        .toValidated { ConfigFailure.Generic("${it.message} to Double") }
                        .map { MeterValue(Duration.of(it.toLong(), ChronoUnit.MILLIS)) }
                        .fold({
                            ConfigFailure.Generic("${v.error.description()}\nOr ${it.description()}").invalid()
                        }) { it.valid() }

                is Validated.Valid -> MeterValue(v.value).valid()
            }
        }

        else -> ConfigFailure.DecodeError(node, type).invalid()
    }
}
